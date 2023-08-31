package com.blog.gxyblog.tool;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/29 17:26
 * @DESCRIPTION:
 */
public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_USER_TTL = 36000L;
    public static final String ARTICLE_PAGE_CODE_KEY = "article:page:code";
    public static final Long ARTICLE_PAGE_TTL = 60L;
    public static final String ARTICLE_CODE_KEY = "article:code";
    public static final String LOCK_ARTICLE_KEY = "article:lock";
    public static final Long COMMON_TTL = 60L;
    public static final String PAGE_CACHE = "page:cache";
    public static final String USER_SIGN_KEY = "user_sign_key";




}
