package com.blog.gxyblog.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

import java.io.Serializable;
import java.util.List;

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
@TableName("t_article")
@ApiModel(value = "Article对象", description = "")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "博客标题")
    private String title;

    @ApiModelProperty(value = "博客正文")
    private String content;

    @ApiModelProperty(value = "博客配图")
    private String image;

    @ApiModelProperty(value = "博客创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "博客更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "0博主，1注册用户，2匿名用户")
    private Long readPermissions;

    @ApiModelProperty(value = "是否公开")
    private Boolean publish;

    @ApiModelProperty(value = "浏览数")
    private Integer views;

    @ApiModelProperty(value = "类型id")
    private Long typeId;

    @ApiModelProperty(value = "博客描述")
    private String description;

    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @ApiModelProperty(value = "逻辑删除")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDel;


}
