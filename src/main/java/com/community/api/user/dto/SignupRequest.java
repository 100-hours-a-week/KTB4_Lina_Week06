package com.community.api.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String nickname;
    @JsonProperty("profile_image")
    private String profileImage;
}
