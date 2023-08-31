package com.blog.gxyblog.exception;

import com.blog.gxyblog.po.ResultCodeEnum;
import lombok.Getter;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/29 22:18
 * @DESCRIPTION:
 */
@Getter
public class BizException extends RuntimeException {
    private final String code;

    public BizException(ResultCodeEnum codeEnum) {
        super(codeEnum.getMsg());
        this.code = codeEnum.getCode();
    }
}
