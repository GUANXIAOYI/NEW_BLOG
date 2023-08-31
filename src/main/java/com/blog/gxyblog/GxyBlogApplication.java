package com.blog.gxyblog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.blog.gxyblog.mapper")
public class GxyBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(GxyBlogApplication.class, args);
    }

}
