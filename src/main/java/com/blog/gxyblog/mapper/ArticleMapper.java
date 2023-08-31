package com.blog.gxyblog.mapper;

import com.blog.gxyblog.dto.Archives;
import com.blog.gxyblog.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
public interface ArticleMapper extends BaseMapper<Article> {

    //文章归档列表
    List<Archives> listArchives();

    //归档查询文章
    List<Article> listArticlesByArchives(Archives archives);
}
