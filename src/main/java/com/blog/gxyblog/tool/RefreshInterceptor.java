package com.blog.gxyblog.tool;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.gxyblog.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

import static com.blog.gxyblog.tool.RedisConstants.LOGIN_USER_TTL;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/29 18:34
 * @DESCRIPTION:
 */
@Slf4j
public class RefreshInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取TOKEN
        String header = request.getHeader("authorization");
        if (StrUtil.isBlank(header)) {
            return true;
        }
        String str = stringRedisTemplate.opsForValue().get(header);
        if (StrUtil.isBlank(str)){
            return true;
        }
        User user = JSONUtil.toBean(str, User.class);
        UserHolder.saveUser(user);
        //刷新有效时间
        stringRedisTemplate.expire(header,LOGIN_USER_TTL, TimeUnit.MINUTES);
        log.info("缓存时间已更新"+String.valueOf(stringRedisTemplate.opsForHash().getOperations().getExpire( header)));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
