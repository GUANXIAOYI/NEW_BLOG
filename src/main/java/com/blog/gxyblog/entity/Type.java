package com.blog.gxyblog.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_type")
@ApiModel(value = "Type对象", description = "")
public class Type implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String name;
    @ApiModelProperty(value = "tag创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "tag更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "逻辑删除0：未删除 1：已删除")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private String isDel;


}
