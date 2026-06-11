package com.community.api.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorInfo {
    private String nickname;
    private String profileImage;
}
