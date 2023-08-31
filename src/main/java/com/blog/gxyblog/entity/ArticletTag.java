package com.blog.gxyblog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 官小一
 * @since 2023-07-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_articlet_tag")
@ApiModel(value="ArticletTag对象", description="")
public class ArticletTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "文章id")
    private Long articleId;

    @ApiModelProperty(value = "标签id")
    private Long tagId;

    @ApiModelProperty(value = "逻辑删除")
    private Integer isDel;


}
