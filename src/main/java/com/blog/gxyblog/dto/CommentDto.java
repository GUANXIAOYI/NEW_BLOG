package com.blog.gxyblog.dto;

import com.blog.gxyblog.entity.Comments;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @AUTHOR: GXY
 * @DATE: 2023/8/14 18:28
 * @DESCRIPTION:
 */
@Data
@Component
public class CommentDto extends Comments {
    private List<Comments> replays;
}
