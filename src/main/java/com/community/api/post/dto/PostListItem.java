package com.community.api.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class PostListItem {
    private Long postId;
    private String title;
    private String image;
    private int likesCount;
    private int viewsCount;
    private int commentsCount;
    private String createdAt;
    private AuthorInfo author;
}
