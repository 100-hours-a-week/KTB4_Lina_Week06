package com.community.api.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private String email;
    private String nickname;
    private String password;
    private String profileImage;
    private Long userId;
}