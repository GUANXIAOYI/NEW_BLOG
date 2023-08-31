package com.blog.gxyblog.controller;


import com.blog.gxyblog.entity.Tag;
import com.blog.gxyblog.service.impl.TagServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private TagServiceImpl tagService;

    //新增标签
    @PostMapping("/admin/inset")
    public String insTage(Tag tag, Model model) {
        model.addAttribute("msg", tagService.saveTag(tag));
        return "html";
    }

    //标签管理界面列表
    @GetMapping("/admin/page")
    public String selByList(@RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                            Model model) {
        model.addAttribute("tags", tagService.SelByPage(currentPage, pageSize));
        return "html";
    }

    //标签删除页面
    @PostMapping("/admin/del/{id}")
    public String delTag(@PathVariable Long id, Model model) {
        model.addAttribute("msg", tagService.delById(id));
        return "HTML";
    }

    //标签更新页面
    @PostMapping("/admin/update")
    public String update(Model model, Tag tag) {
        model.addAttribute("msg", tagService.updateTag(tag));
        return "html";
    }

    //通过标签查询博客
    @GetMapping("/searchArticle/{id}")
    public String getArticlesByTag(@PathVariable Long id, Model model) {
        model.addAttribute("ArticlesByTag", tagService.selArticleByTag(id));
        return "html";
    }
}

