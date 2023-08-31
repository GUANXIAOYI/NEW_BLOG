package com.blog.gxyblog.Config;

import com.blog.gxyblog.tool.LoginInterceptor;
import com.blog.gxyblog.tool.RefreshInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/29 19:26
 * @DESCRIPTION:
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("").setViewName("");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截所有请求进行token刷新
        registry.addInterceptor(new RefreshInterceptor()).order(0);
        //拦截需要进行后台验证的请求
        // registry.addInterceptor(new LoginInterceptor()).addPathPatterns(
        //         "/background/**"
        // ).order(1);
    }
}
