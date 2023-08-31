package com.blog.gxyblog.tool;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/29 17:45
 * @DESCRIPTION:
 */
public class CommonConstants {
    public static final String REGEX = "^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$";
    public static final String REGEX_PASSWORD = " /^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[^]{8,16}$/";
    public static final Long ANONYMOUS_USER_DEFAULT_LEVEL = 2L;
    public static final Integer MAX_TAG_SIZE = 21;

}
