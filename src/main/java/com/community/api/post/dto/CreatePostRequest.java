package com.community.api.post.dto;

import lombok.Getter;

@Getter
public class CreatePostRequest {
    private String title;
    private String content;
    private String image;
}
