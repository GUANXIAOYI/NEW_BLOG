package com.blog.gxyblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.gxyblog.dto.ArticleDto;
import com.blog.gxyblog.entity.Article;
import com.blog.gxyblog.entity.ArticletTag;
import com.blog.gxyblog.entity.Tag;
import com.blog.gxyblog.entity.Type;
import com.blog.gxyblog.mapper.TypeMapper;
import com.blog.gxyblog.service.TypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {
    @Autowired
    private ArticleServiceImpl articleService;
    @Autowired
    private ArticletTagServiceImpl articletTagService;
    @Autowired
    private TagServiceImpl tagService;

    @Override
    public String saveType(Type type) {
        //判断标签名是否重复
        Integer count = this.query().eq("name", type.getName()).count();
        int size = this.list().size();
        if (count >= 1) {
            return "标签名字不能重复";
        } else if (size == MAX_TAG_SIZE) {
            return "已经达到最大数量标签";
        }
        this.save(type);
        return "标签保存成功";
    }

    //分页
    @Override
    public List<Type> SelByPage(Integer currentPage, Integer pageSize) {
        //给page分页
        Page<Type> typePage = new Page<>();
        LambdaQueryWrapper<Type> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Type::getCreateTime);
        this.page(typePage, queryWrapper);
        return typePage.getRecords();
    }

    //删除
    @Override
    public String delById(Long id) {
        Integer count = articleService.query().eq("type_id", id).count();
        if (count != 0) {
            return "分类正在被博文引用不能删除";
        }
        this.delById(id);
        return "分类删除成功";
    }

    //更新
    @Override
    public String updateType(Type type) {
        Integer count = this.query().eq("name", type.getName()).count();
        if (count >= 1) {
            return "标签名字不能重复";
        }
        this.updateById(type);
        return "标签更新成功";
    }

    //根据分类搜索
    @Override
    public List<ArticleDto> selArticleByType(Long id) {
        List<Article> articles = articleService.query().eq("type_id", id).list();
        List<ArticleDto> articleDtos = articles.stream().map(model -> {
            ArticleDto articleDto = new ArticleDto();
            BeanUtil.copyProperties(model, articleDto);
            //和标签的关系表
            List<ArticletTag> list = articletTagService.query().eq("article_id", model.getId()).list();
            List<Tag> tags = new ArrayList<>();
            for (ArticletTag articletTag : list) {
                Tag tag = tagService.getById(articletTag.getTagId());
                tags.add(tag);
            }
            //封装分类和标签名字
            articleDto.setTypeName(getById(id).getName());
            articleDto.setTags(tags);
            return articleDto;
        }).collect(Collectors.toList());
        return articleDtos;
    }
}
