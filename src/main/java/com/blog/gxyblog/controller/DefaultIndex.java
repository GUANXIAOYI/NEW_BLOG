package com.blog.gxyblog.controller;

import com.blog.gxyblog.service.impl.ArticleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/8/9 13:41
 * @DESCRIPTION:
 */
@Controller
public class DefaultIndex {
    @Autowired
    private ArticleServiceImpl articleService;

    @RequestMapping("/")
    public String hello(@RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                        Model model) {
        model.addAttribute("articles", articleService.getArticleByPage(currentPage, pageSize));
        return "index";
    }
}
