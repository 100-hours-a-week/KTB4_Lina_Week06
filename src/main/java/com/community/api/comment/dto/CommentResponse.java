package com.community.api.comment.dto;

import com.community.api.post.dto.AuthorInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CommentResponse {
    private Long commentId;
    private String content;
    private String createdAt;
    private AuthorInfo author;
}
