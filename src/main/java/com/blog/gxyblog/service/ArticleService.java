package com.blog.gxyblog.service;

import com.blog.gxyblog.dto.Archives;
import com.blog.gxyblog.dto.ArticleDto;
import com.blog.gxyblog.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
public interface ArticleService extends IService<Article> {
    //返回首页分页查询的文章
    List<ArticleDto> getArticleByPage(Integer currentPage, Integer pageSize);

    //查看文章详情
    ArticleDto selectArticleBYDetails(Long id);

    //插入文章
    String putArticleAndTag(ArticleDto articleDto);

    //删除文章
    String delById(Long id);

    //更新文章
    String updateByArticle(ArticleDto articleDto);

    //全文搜索
    List<ArticleDto> selByEs(String text);

    List<Archives> selByArchives();

    //返回归档的数据
    List<Article> selArticleByArchives(Archives archives);
}
