package com.blog.gxyblog.controller;


import com.blog.gxyblog.entity.Tag;
import com.blog.gxyblog.entity.Type;
import com.blog.gxyblog.service.impl.TypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
@Controller
@RequestMapping("/type")
public class TypeController {
    @Autowired
    private TypeServiceImpl typeService;

    //新增分类
    @PostMapping("/admin/inset")
    public String insTage(Type type, Model model) {
        model.addAttribute("msg", typeService.saveType(type));
        return "html";
    }

    //分类管理界面列表
    @GetMapping("/admin/page")
    public String selByList(@RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                            Model model) {
        model.addAttribute("types", typeService.SelByPage(currentPage, pageSize));
        return "html";
    }

    //分类删除页面
    @PostMapping("/admin/del/{id}")
    public String delType(@PathVariable Long id, Model model) {
        model.addAttribute("msg", typeService.delById(id));
        return "HTML";
    }
    //分类更新页面
    @PostMapping("/admin/update")
    public String update(Model model,Type type){
        model.addAttribute("msg",typeService.updateType(type));
        return "html";
    }
    //通过type找文章

    @GetMapping("/searchArticle/{id}")
    public String selArticleByid(@PathVariable Long id,Model model){
        model.addAttribute("ArticleByType",typeService.selArticleByType(id));
        return "html";
    }
}

