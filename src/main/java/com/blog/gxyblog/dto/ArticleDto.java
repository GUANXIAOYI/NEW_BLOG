package com.blog.gxyblog.dto;

import com.blog.gxyblog.entity.Article;
import com.blog.gxyblog.entity.Tag;
import lombok.Data;

import java.util.List;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/7/30 13:37
 * @DESCRIPTION:
 */
@Data
public class ArticleDto extends Article {
    private String typeName;
    private List<Tag> tags;
}
