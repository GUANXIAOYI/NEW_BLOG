package com.blog.gxyblog.controller;


import com.blog.gxyblog.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;


import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserServiceImpl userService;

    //注册功能
    @PostMapping("/register")
    public String register(String username, String password, String nickname, Model model) {
        //通知消息
        String msg = userService.register(username, password, nickname);
        model.addAttribute("msg", msg);
        return "html";
    }

    //登录功能
    @PostMapping("/login")
    public String login(String username, String password, String code, Model model,
                        @RequestParam(defaultValue = "false", required = false) Boolean remember) {
        //通知消息
        String msg = userService.userLogin(username, password, code,remember);
        model.addAttribute("msg", msg);
        return "html";
    }

    //    根据用户名获取验证码
    @PostMapping("/code")
    public String getCode(String username, String password, Model model) {
        //通知消息
        String msg = userService.validate(username, password);
        model.addAttribute("msg", msg);
        return "html";
    }

    //用户签到功能
    @PostMapping("/sign")
    public String sign(Model model) {
        userService.sign();
        model.addAttribute("msg", userService.sign());
        return "html";
    }

    //用户统计签到功能
    @GetMapping("/count/sign")
    public String countSign(Model model) {
        model.addAttribute("msg", userService.countSign());
        return "html";
    }
}

