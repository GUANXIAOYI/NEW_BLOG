package com.blog.gxyblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.gxyblog.common.EsUtils;
import com.blog.gxyblog.dto.Archives;
import com.blog.gxyblog.dto.ArticleDto;
import com.blog.gxyblog.entity.*;
import com.blog.gxyblog.exception.BizException;
import com.blog.gxyblog.mapper.ArticleMapper;
import com.blog.gxyblog.po.ResultCodeEnum;
import com.blog.gxyblog.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.gxyblog.tool.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.blog.gxyblog.Config.EsClient.*;
import static com.blog.gxyblog.tool.CommonConstants.ANONYMOUS_USER_DEFAULT_LEVEL;
import static com.blog.gxyblog.tool.RedisConstants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private TypeServiceImpl typeService;
    @Resource
    private TagServiceImpl tagService;
    @Autowired
    private ArticletTagServiceImpl articletTagService;
    @Autowired
    private EsUtils esUtils;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    //返回首页分页查询的文章
    public List<ArticleDto> getArticleByPage(Integer currentPage, Integer pageSize) {
        //查询缓存
        String str = stringRedisTemplate.opsForValue().get(ARTICLE_PAGE_CODE_KEY + currentPage);
        if (StrUtil.isNotBlank(str)) {
            return JSONUtil.toList(str, ArticleDto.class);
        }
        //查询前十条数据
        Page<Article> pageInfo = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Article> query = new LambdaQueryWrapper<>();
        query.orderByDesc(Article::getCreateTime);
        List<Article> articles = this.page(pageInfo, query).getRecords();

        //封装Article
        List<ArticleDto> articleDtos = getArticleDto(articles);
        if (articleDtos.isEmpty()) {
            throw new BizException(ResultCodeEnum.ARTICLE_NULL_ERROR);
        }
        //将查询出来的对象按照每页做缓存
        for (ArticleDto articleDto : articleDtos) {
            String key = articleDto.getId().toString();
        }
        stringRedisTemplate.opsForValue().set(ARTICLE_PAGE_CODE_KEY + currentPage, JSONUtil.toJsonStr(articleDtos), ARTICLE_PAGE_TTL, TimeUnit.MINUTES);
        return articleDtos;
    }

    //查看文章详情
    @Override
    public ArticleDto selectArticleBYDetails(Long id) {
        String key = ARTICLE_CODE_KEY + id;
        //默认用户权限等级
        Long userLeve = ANONYMOUS_USER_DEFAULT_LEVEL;
        User user = UserHolder.getUser();
        if (user != null) {
            userLeve = user.getPermission();
        }
        //查询缓存判断权限
        String cache = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(cache)) {
            ArticleDto articleDto = JSONUtil.toBean(cache, ArticleDto.class);
            if (userLeve > articleDto.getReadPermissions()) {
                throw new BizException(ResultCodeEnum.ARTICLE_LEVEL_ERROR);
            }
            return articleDto;
        }
        //如果是查询数据库不存在的空缓存直接返回null
        if (cache != null) {
            return null;
        }
        Boolean lock = tryLock(LOCK_ARTICLE_KEY + id);
        //获取锁失败带表其他线程在对缓存进行重构
        Article article = getById(id);
        ArticleDto articleDto = new ArticleDto();
        try {
            if (!lock) {
                Thread.sleep(50);
                selectArticleBYDetails(id);
            }
            if (article == null) {
                stringRedisTemplate.opsForValue().set(key, "", COMMON_TTL, TimeUnit.MINUTES);
                return null;
            }
            //封装数据
            BeanUtil.copyProperties(article, articleDto);
            articleDto.setTypeName(typeService.getById(article.getTypeId()).getName());
            //获取文章对应的标签
            List<ArticletTag> articletTagList = articletTagService.query().eq("article_id", id).list();
            List<Tag> tags = articletTagList.stream().map(item -> {
                return tagService.getById(item.getTagId());
            }).collect(Collectors.toList());
            articleDto.setTags(tags);
            //加入缓存
            //保存成功后使用mq进行redis和Es同步
            EXECUTOR.submit(new Runnable() {
                @Override
                public void run() {
                    rabbitTemplate.convertAndSend(EXCHANGE_PUT, EXCHANGE_PUT_KEY, articleDto);//同步到redis缓存
                }
            });
            //煤油权限也先加入缓存
            if (userLeve > article.getReadPermissions()) {
                throw new BizException(ResultCodeEnum.ARTICLE_LEVEL_ERROR);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stringRedisTemplate.delete(LOCK_ARTICLE_KEY + id);
        }
        return articleDto;
    }

    @Override
    @Transactional
    public String putArticleAndTag(ArticleDto articleDto) {
        boolean save = save(articleDto);
        if (!save) {
            log.error("保存失败id:{},标题:{}", articleDto.getId(), articleDto.getTitle());
            return "文章保存失败";
        }
        //构建文章和标签的关系表
        List<ArticletTag> collect = getArticletTags(articleDto);
        boolean saveBatch = articletTagService.saveBatch(collect);
        if (!saveBatch) {
            log.error("保存文章对应tag失败文章id:{},文章标题:{}", articleDto.getId(), articleDto.getTitle());
            return "保存文章对应标签失败";
        }
        //保存成功后使用mq进行redis和Es同步
        EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                rabbitTemplate.convertAndSend(EXCHANGE_PUT, EXCHANGE_PUT_KEY, articleDto);//同步到redis缓存
            }
        });
        return "数据保存成功";
    }

    @Override
    public String delById(Long id) {
        // 删除对应的tag和文章关系表
        List<Long> longs = articletTagService.query().eq("article_id", id).list().stream()
                .map(ArticletTag::getId).collect(Collectors.toList());
        articletTagService.removeByIds(longs);
        //删除文章本体
        this.removeById(id);
        //保存成功后使用mq进行redis和Es同步
        EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                rabbitTemplate.convertAndSend(EXCHANGE_DEL, EXCHANGE_DEL_KEY, id);//同步到redis缓存和Es
            }
        });
        return "删除数据成功";
    }

    @Override
    @Transactional
    public String updateByArticle(ArticleDto articleDto) {
        //更新基础文章内容
        save(articleDto);
        //删除之前和标签的关联关系
        articletTagService.removeByIds(articletTagService.query().eq("article_id", articleDto.getId()).list().stream()
                .map(ArticletTag::getId).collect(Collectors.toList()));
        //构建标签和文章的关系
        List<ArticletTag> collect = getArticletTags(articleDto);
        //存入关系表
        articletTagService.saveBatch(collect);
        //更新redis缓存
        EXECUTOR.submit(new Runnable() {
            @Override
            public void run() {
                rabbitTemplate.convertAndSend(EXCHANGE_PUT, EXCHANGE_PUT_KEY, articleDto);//更新到redis和ES缓存
            }
        });

        return "更新数据成功";
    }

    //通过es全文检索查询数据
    @Override
    public List<ArticleDto> selByEs(String text) {
        List<Article> articles = esUtils.selByEs(text);
        List<ArticleDto> articleDto = getArticleDto(articles);
        return articleDto;
    }

    //文章归档总数
    @Override
    public List<Archives> selByArchives() {
        return articleMapper.listArchives();

    }

    @Override
    public List<Article> selArticleByArchives(Archives archives) {
        //归档数据
        return articleMapper.listArticlesByArchives(archives);
    }

    //封装数据
    private List<ArticleDto> getArticleDto(List<Article> articles) {
        //封装Article
        List<ArticleDto> articleDtos = articles.stream().map(item -> {
            //查找和文章关联的id
            LambdaQueryWrapper<ArticletTag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ArticletTag::getArticleId, item.getId());
            List<ArticletTag> articletTags = articletTagService.list(queryWrapper);
            //文章中所有的标签
            List<Tag> tags = articletTags.stream().map(model -> {
                return tagService.getById(model.getTagId());
            }).collect(Collectors.toList());

            //返回数据封装
            ArticleDto articleDto = new ArticleDto();
            Type type = typeService.getById(item.getTypeId());
            BeanUtil.copyProperties(item, articleDto);
            articleDto.setTypeName(type.getName());
            articleDto.setTags(tags);
            return articleDto;
        }).collect(Collectors.toList());
        return articleDtos;
    }

    //获取文章对应表关系
    private List<ArticletTag> getArticletTags(ArticleDto articleDto) {
        return articleDto.getTags().stream().map(item -> {
            ArticletTag articletTag = new ArticletTag();
            Long tagId = item.getId();
            articletTag.setTagId(tagId);
            articletTag.setArticleId(articleDto.getId());
            return articletTag;
        }).collect(Collectors.toList());
    }

    //获取互斥锁
    private Boolean tryLock(String key) {
        Boolean ifAbsent = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.MINUTES);
        boolean result = BooleanUtil.isTrue(ifAbsent);
        return result;
    }
}
