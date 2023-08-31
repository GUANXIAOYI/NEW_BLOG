package com.blog.gxyblog.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.io.Serializable;
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
@TableName("t_comments")
@ApiModel(value="Comments对象", description="")
public class Comments implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名字")
    private String userName;

    @ApiModelProperty(value = "回复的用户id")
    private Long replyUserId;

    @ApiModelProperty(value = "回复的用户名字")
    private String replyUserName;

    @ApiModelProperty(value = "回复评论的id")
    private Long replyCommentId;

    @ApiModelProperty(value = "文章的id")
    private Long articleId;

    @ApiModelProperty(value = "回复时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "回复类容")
    private String comment;

    @ApiModelProperty(value = "父id：0表示一级，其他表示为一级评论id")
    private Long parentCommentId;

    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

    @ApiModelProperty(value = "逻辑删除0:未删除：已经删除")
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDel;


}
