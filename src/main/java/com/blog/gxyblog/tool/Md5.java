package com.blog.gxyblog.tool;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/29 19:54
 * @DESCRIPTION:
 */
public class Md5 {
    /** * MD5加密之方法一 * @explain 借助apache工具类DigestUtils实现 * @param str * 待加密字符串 * @return 16进制加密字符串 */
    public static String encryptToMD5(String str) {
        return DigestUtils.md5Hex(str);
    }


}
