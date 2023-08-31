package com.blog.gxyblog.service;

import com.blog.gxyblog.dto.ArticleDto;
import com.blog.gxyblog.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
public interface TagService extends IService<Tag> {
    //保存标签
    String saveTag(Tag tag);

    List<Tag> SelByPage(Integer currentPage, Integer pageSize);

    String delById(Long id);

    String updateTag(Tag tag);

    List<ArticleDto> selArticleByTag(Long id);
}
