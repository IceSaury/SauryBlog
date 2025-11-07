package com.saury.blog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论VO
 *
 * @author Saury
 */
@Data
public class CommentVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 回复用户ID
     */
    private Long replyUserId;

    /**
     * 回复用户昵称
     */
    private String replyUserNickname;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 归属地
     */
    private String address;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 子评论列表
     */
    private List<CommentVO> children;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}


