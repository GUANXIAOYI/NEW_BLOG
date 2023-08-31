package com.blog.gxyblog.po;

import com.blog.gxyblog.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sun.security.util.Password;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/29 22:29
 * @DESCRIPTION:
 */
@AllArgsConstructor
@Getter
public enum ResultCodeEnum {
    //通用提示
    SUCCESS("10001", "操作成功"),
    FAIL("10002", "操作失败"),

    //用户异常模块
    USER_EXIST("11002", "注册失败，邮箱已存在"),
    PASSWORD_ERROR("11003", "请输入至少8~16包含大小写字母的密码"),
    USERNAME_ERROR("11004", "邮箱或用户名格式错误"),
    CODE_FAILURE("11005", "登录验证码失效"),
    ACCOUNT_ERROR("11006", "账号或者密码错误"),
    USER_NOT_EXIST("11007", "账号不存在"),

    //文章异常模块
    ARTICLE_NULL_ERROR("22001", "文章数据异常"),
    ARTICLE_LEVEL_ERROR("22002", "用户权限不足"),

    //文件上传异常
    FILE_UPLOAD_ERROR("33001", "文件上传失败"),
    FILE_LOAD_ERROR("33001", "文件读取失败"),
    //全文检索异常
    ES_INDICES_NULL("44001","索引不存在"),
    ES_SEARCH_ERROR("44002","全文检索异常"),
    ES_INSERT_ERROR("44002","全文检索异常"),
    ;


    private final String code;
    private final String msg;

}
