package com.blog.gxyblog.service;

import com.blog.gxyblog.dto.ArticleDto;
import com.blog.gxyblog.entity.Type;
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
public interface TypeService extends IService<Type> {

    String saveType(Type type);

    List<Type> SelByPage(Integer currentPage, Integer pageSize);

    String delById(Long id);

    String updateType(Type type);

    List<ArticleDto> selArticleByType(Long id);
}
