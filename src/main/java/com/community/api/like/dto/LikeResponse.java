package com.community.api.like.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeResponse {
    private int likesCount;
    private boolean liked;
}