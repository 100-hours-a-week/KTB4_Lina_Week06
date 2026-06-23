package com.community.api.post.dto;

import com.community.api.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorInfo {
    private String nickname;
    private String profileImage;

    public static AuthorInfo from(User user) {
        // 탈퇴한 사용자
        if (user == null || user.isDeleted()) {
            return AuthorInfo.builder()
                    .nickname("알수없음")
                    .profileImage(null)
                    .build();
        }
        return AuthorInfo.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }
}
