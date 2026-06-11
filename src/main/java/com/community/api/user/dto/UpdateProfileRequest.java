package com.community.api.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UpdateProfileRequest {
    private String nickname;
    @JsonProperty("profile_image")
    private String profileImage;
}