package com.blog.gxyblog.Config;

import com.blog.gxyblog.exception.BizException;
import com.blog.gxyblog.po.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/29 22:16
 * @DESCRIPTION:
 */
@Slf4j
@ControllerAdvice("com.blog.gxyblog.controller")
public class GlobExceptionHandler {

    //业务异常全局处理方法
    @ExceptionHandler(BizException.class)
    public ModelAndView bizExceptionHandler(BizException ex) {
        log.error("异常code：{},异常msg：{}", ex.getCode(), ex.getMessage());
        ModelAndView view = new ModelAndView();
        view.addObject("code", ex.getCode());
        view.addObject("msg", ex.getMessage());
        view.setViewName("error/500");
        return view;
    }
}
