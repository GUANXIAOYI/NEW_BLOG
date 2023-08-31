package com.blog.gxyblog.tool;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * @AUTHOR: GXY
 * @DATE: 2022/12/1 14:08
 * @DESCRIPTION:
 * 功能字段自动填充
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        setFieldValByName("isDel", 0,metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共自动自动填充");
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

}
