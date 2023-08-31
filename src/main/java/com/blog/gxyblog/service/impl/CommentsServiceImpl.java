package com.blog.gxyblog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.blog.gxyblog.dto.CommentDto;
import com.blog.gxyblog.entity.Comments;
import com.blog.gxyblog.mapper.CommentsMapper;
import com.blog.gxyblog.service.CommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 官小一
 * @since 2023-07-29
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements CommentsService {

    //通过文化章查看当前存在的id
    //保存评论
    @Override
    public String saveComment(Comments comments) {
        //获取评论的父id
        Long parentCommentId = comments.getParentCommentId();
        //如果父id不为-1表示是子id
        if (parentCommentId == -1) {
            comments.setParentCommentId(-1L);
        }
        this.save(comments);
        return "提交成功";
    }

    // 查询所有id进行二级评论分类
    @Override
    public List<CommentDto> selCommentsByArticle(Long id) {
        List<Comments> comments = this.query().eq("article_id", id).orderByDesc().list();
        return eachComment(comments);
    }

    //防止内部数据出错进行数据Copy
    private List<CommentDto> eachComment(List<Comments> comments) {
        List<CommentDto> webViewComments = new ArrayList<>();
        for (Comments comment : comments) {
            Comments view = new Comments();
            BeanUtil.copyProperties(comment, view);
            //封装dto
            CommentDto commentDto = new CommentDto();
            BeanUtil.copyProperties(comment, commentDto);
            webViewComments.add(commentDto);
        }
        //合并评论的各层子代到第一级子代集合中
        combineChildren(webViewComments);
        return webViewComments;
    }

    //找出评论的子集

    private void combineChildren(List<CommentDto> webViewComments) {
        for (CommentDto comment : webViewComments) {
            //所有评论的子集
            List<Comments> replays = this.query().eq("parent_comment_id", comment.getId()).list();
            for (Comments replay : replays) {
                //循环迭代，找出子代，存放在tempReplys中
                recursively(replay);
            }
            //给父节点的reply集合赋值
            comment.setReplays(tempReplys);//一条评论和他的最总子集
            //清除临时存放区
            tempReplys = new ArrayList<>();

        }
    }
    //存放迭代找出的所有子代的集合
    private List<Comments> tempReplys = new ArrayList<>();

    private void recursively(Comments replay) {
        //第一次遍历出来的子集
        tempReplys.add(replay);
        //判断子集是否还有子集
        //如果有子集就继续遍历
        if (replay.getParentCommentId() != null) {
            List<Comments> replays = this.query().eq("parent_comment_id", replay.getId()).list();
            for (Comments comments : replays) {
                //第二次遍历出来的子集
                tempReplys.add(comments);
                if (comments.getParentCommentId() != null) {
                    recursively(comments);//如果这条评论还存在子集就继续递归
                }
            }
        }
    }
}
