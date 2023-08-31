package com.blog.gxyblog.service;

import com.blog.gxyblog.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
public interface UserService extends IService<User> {

    //获取验证码返回值
    String validate(String username, String password);

    //登录结果返回值
    String userLogin(String username, String password,String code,Boolean remember);

    //注册结果返回值
    String register(String username, String password,String nickname);

    String sign();

    String countSign();
}
