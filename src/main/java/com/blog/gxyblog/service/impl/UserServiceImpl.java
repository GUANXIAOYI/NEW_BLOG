package com.blog.gxyblog.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.gxyblog.entity.User;
import com.blog.gxyblog.exception.BizException;
import com.blog.gxyblog.mapper.UserMapper;
import com.blog.gxyblog.po.ResultCodeEnum;
import com.blog.gxyblog.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.gxyblog.tool.Md5;
import com.blog.gxyblog.tool.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.blog.gxyblog.tool.CommonConstants.REGEX;
import static com.blog.gxyblog.tool.CommonConstants.REGEX_PASSWORD;
import static com.blog.gxyblog.tool.RedisConstants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${spring.mail.username}")
    private String from;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);


    @Override
    public String userLogin(String username, String password, String verificationCode,Boolean remember) {
        User user = getUser(username);
        if (user == null) {
            return "请注册用户";
        }
        //判断验证码是否过期
        String code = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + username);
        if (StrUtil.isBlank(code) || !code.equals(verificationCode)) {
            throw new BizException(ResultCodeEnum.CODE_FAILURE);
        }
        if (!user.getPassword().equals(password)) {
            throw new BizException(ResultCodeEnum.ACCOUNT_ERROR);
        }
        //生成token
        String token = UUID.randomUUID().toString(true) + username;
        //将对象序列化
        String jsonStr = JSONUtil.toJsonStr(user);
        stringRedisTemplate.opsForValue().set(token, jsonStr, LOGIN_USER_TTL, TimeUnit.MINUTES);
        return token;
    }

    //创建用户
    @Override
    @Transactional
    public String register(String username, String password, String nickname) {
        //效验邮箱格式
        boolean matches = username.matches(REGEX);
        if (!matches || nickname.length() > 10) {
            throw new BizException(ResultCodeEnum.USERNAME_ERROR);
        }
        //效验密码格式
        if (!password.matches(REGEX_PASSWORD)) {
            throw new BizException(ResultCodeEnum.PASSWORD_ERROR);
        }
        //判断用户是否存在
        if (getUser(username) != null) {
            throw new BizException(ResultCodeEnum.USER_EXIST);
        }
        String md5 = Md5.encryptToMD5(password);
        User user = new User();
        user.setUserName(username);
        user.setPassword(md5);
        user.setNickname(nickname);
        boolean result = save(user);
        return "创建用户成功！";
    }

    @Override
    public String sign() {
        // 1.获取当前登录用户
        Long userId = UserHolder.getUser().getId();
        // 2.获取日期
        LocalDateTime now = LocalDateTime.now();

        // 3.拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4.获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5.写入Redis SETBIT key offset 1
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
        return "签到成功";
    }

    @Override
    public String countSign() {
        // 1.获取当前登录用户
        Long userId = UserHolder.getUser().getId();
        // 2.获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3.拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4.获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5.获取本月截止今天为止的所有的签到记录，返回的是一个十进制的数字
        List<Long> result = stringRedisTemplate.opsForValue().bitField(key, BitFieldSubCommands.create()
                .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0));

        if (result == null || result.isEmpty()) {
            // 没有任何签到结果
            return "没有任何签到结果";
        }
        Long num = result.get(0);
        if (num == null || num == 0) {
            return "没有任何签到结果";

        }
        //
        int count = 0;
        while (true) {
            // 让这个数字与1做与运算，得到数字的最后一个bit位  // 判断这个bit位是否为0
            if ((num & 1) == 0) {
                // 如果为0，说明未签到，结束
                break;
            } else {
                // 如果不为0，说明已签到，计数器+1
                count++;
            }
            // 把数字右移一位，抛弃最后一个bit位，继续下一个bit位
            num >>>= 1;
        }
        return "当前共计签到：" + count + "天";
    }


    @Override
    public String validate(String username, String password) {

        //效验邮箱格式
        boolean matches = username.matches(REGEX);
        if (!matches) {
            throw new BizException(ResultCodeEnum.USERNAME_ERROR);
        }

        User user = getUser(username);
        if (user == null) {
            throw new BizException(ResultCodeEnum.USER_NOT_EXIST);
        }
        return getCode(username);

    }

    //生成随机验证码发送至邮箱
    private String getCode(String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        String code = RandomUtil.randomNumbers(6);
        log.info("验证码：{}", code);
        //设置验证码有效时间
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + username, code, 60, TimeUnit.SECONDS);

        message.setSubject("博客登录验证码");
        message.setFrom(from);
        LocalDateTime now = LocalDateTime.now().plusSeconds(60);
        message.setText("恭喜成功获取验证码：" + code + ",有效时间60秒至" + now + "");
        message.setTo(username);
        javaMailSender.send(message);
        log.info("验证码发送成功");
        return "验证码发送成功";

    }

    private User getUser(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        User user = getOne(queryWrapper);
        return user;
    }
}
