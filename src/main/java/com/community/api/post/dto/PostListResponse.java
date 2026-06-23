package com.community.api.post.dto;

import com.community.api.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostListResponse {
    private List<Post> posts;
    private int page;
    private int limit;
    private int total;
    private boolean hasNext;
}