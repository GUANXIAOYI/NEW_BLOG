package com.blog.gxyblog;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class GxyBlogApplicationTests {


    @Test
    void contextLoads() {
        System.out.println(1);
    }
}
