package com.blog.gxyblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.gxyblog.dto.ArticleDto;
import com.blog.gxyblog.entity.Article;
import com.blog.gxyblog.entity.ArticletTag;
import com.blog.gxyblog.entity.Tag;
import com.blog.gxyblog.mapper.TagMapper;
import com.blog.gxyblog.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.blog.gxyblog.tool.CommonConstants.MAX_TAG_SIZE;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    private ArticletTagServiceImpl articletTagService;
    @Autowired
    private ArticleServiceImpl articleService;
    @Autowired
    private TypeServiceImpl typeService;

    @Override
    public String saveTag(Tag tag) {
        //判断标签名是否重复
        Integer count = this.query().eq("name", tag.getName()).count();
        int size = this.list().size();
        if (count >= 1) {
            return "标签名字不能重复";
        } else if (size == MAX_TAG_SIZE) {
            return "已经达到最大数量标签";
        }
        this.save(tag);
        return "标签保存成功";
    }

    //标签分页查询
    @Override
    public List<Tag> SelByPage(Integer currentPage, Integer pageSize) {
        //给page分页
        Page<Tag> tagPage = new Page<>();
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Tag::getCreateTime);
        this.page(tagPage, queryWrapper);
        return tagPage.getRecords();

    }

    //删除标签
    @Transactional
    @Override
    public String delById(Long id) {
        Integer count = articletTagService.query().eq("tag_id", id).count();
        if (count != 0) {
            return "标签正在被博文引用不能删除";
        }
        this.delById(id);
        return "标签删除成功";
    }

    @Override
    public String updateTag(Tag tag) {
        Integer count = this.query().eq("name", tag.getName()).count();
        if (count >= 1) {
            return "标签名字不能重复";
        }
        this.updateById(tag);
        return "标签更新成功";
    }

    @Override
    @Transactional
    public List<ArticleDto> selArticleByTag(Long id) {
        //关系表
        List<ArticletTag> articletTags = articletTagService.query().eq("tag_id", id).list();
        //博客对应文章id
        List<Long> articleIds = articletTags.stream().map(ArticletTag::getArticleId).collect(Collectors.toList());
        //对应博客
        List<Article> list = articleService.query().in("id", articleIds).orderByDesc("create_time").list();
        List<ArticleDto> articleDtos = list.stream().map(model -> {
            ArticleDto articleDto = new ArticleDto();
            BeanUtil.copyProperties(model, articleDto);
            //对应标签
            List<ArticletTag> article_id = articletTagService.query().eq("article_id", model.getId()).list();
            List<Tag> tags = new ArrayList<>();
            for (ArticletTag articletTag : article_id) {
                Tag tag = getById(articletTag.getTagId());
                tags.add(tag);
            }
            articleDto.setTypeName(typeService.getById(model.getTypeId()).getName());
            articleDto.setTags(tags);
            return articleDto;

        }).collect(Collectors.toList());
        return articleDtos;
    }
}
