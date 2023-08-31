package com.blog.gxyblog.service;

import com.blog.gxyblog.dto.CommentDto;
import com.blog.gxyblog.entity.Comments;
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
public interface CommentsService extends IService<Comments> {

    //保存评论
    String saveComment(Comments comments);

    //通过文章id查询博客
    List<CommentDto> selCommentsByArticle(Long id);
}
