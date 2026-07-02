package com.community.api.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListResponse {
    private List<PostListItem> posts;
    private int page;
    private int limit;
    private int total;
    private boolean hasNext;
}