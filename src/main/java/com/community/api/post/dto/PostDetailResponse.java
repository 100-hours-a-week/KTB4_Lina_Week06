package com.community.api.post.dto;

import com.community.api.comment.Comment;
import com.community.api.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
public class PostDetailResponse {
    private Long postId;
    private String title;
    private String content;
    private String image;
    private int likesCount;
    private int viewsCount;
    private int commentsCount;
    private String createdAt;
    private AuthorInfo author;
    private List<Comment> comments;
}