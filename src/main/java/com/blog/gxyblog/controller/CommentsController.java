package com.blog.gxyblog.controller;


import com.blog.gxyblog.entity.Comments;
import com.blog.gxyblog.service.impl.CommentsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
@Controller
@RequestMapping("/comments")
public class CommentsController {
    @Autowired
    private CommentsServiceImpl commentsService;

    @PostMapping("/replay")
    public String replay(Comments comments, Model model) {
        model.addAttribute("msg", commentsService.saveComment(comments));
        return "html";
    }
    @GetMapping("/article/replay/{id}")
    public String replayByArticle(@PathVariable Long id,Model model){
        model.addAttribute("comments",commentsService.selCommentsByArticle(id));
        return "html";
    }
}

