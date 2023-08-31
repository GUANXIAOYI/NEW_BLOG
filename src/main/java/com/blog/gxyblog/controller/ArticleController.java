package com.blog.gxyblog.controller;


import com.blog.gxyblog.common.EsUtils;
import com.blog.gxyblog.dto.Archives;
import com.blog.gxyblog.dto.ArticleDto;
import com.blog.gxyblog.entity.Article;
import com.blog.gxyblog.service.impl.ArticleServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
@Controller
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleServiceImpl articleService;

    @Autowired
    private EsUtils esUtils;


    //分页显示类容
    @GetMapping("/list")
    public String getArticles(@RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              Model model) {
        model.addAttribute("articles", articleService.getArticleByPage(currentPage, pageSize));
        return "index";
    }

    //查看文章详情
    @GetMapping("/details/{id}")
    public String getArticle(@PathVariable Long id, Model model) {
        model.addAttribute("article", articleService.selectArticleBYDetails(id));
        return "html";
    }

    //增加文章
    @PutMapping("/admin/save/article")
    public String putArticle(ArticleDto articleDto, Model model) {

        model.addAttribute("msg", articleService.putArticleAndTag(articleDto));
        return "html";
    }

    //删除博客
    @DeleteMapping("/admin/del/{id}")
    public String delArticle(Model model, @PathVariable Long id) {

        model.addAttribute("msg", articleService.delById(id));
        return "html";
    }

    //更新博客
    @PutMapping("/admin/update")
    public String update(ArticleDto articleDto, Model model) {
        model.addAttribute("msg", articleService.updateByArticle(articleDto)
        );
        return "html";
    }

    //归档查询
    @GetMapping("/archives/search")
    public String selByArchives(Model model) {
        model.addAttribute("archives", articleService.selByArchives());
        return "html";
    }

    //根据时间归档查询文章
    @GetMapping("/archives/article/search")
    public String selArticleByArchives(Model model, Archives archives) {
        model.addAttribute("articlesByArchives", articleService.selArticleByArchives(archives));
        return "html";
    }


    //全文检索
    @GetMapping("/search")
    public String selByEs(String text, Model model) {
        model.addAttribute("articlesByEs", articleService.selByEs(text));
        return "html";
    }


}

