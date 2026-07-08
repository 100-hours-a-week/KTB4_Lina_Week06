package com.community.api.like;

import com.community.api.auth.CustomUserDetails;
import com.community.api.common.ApiResponse;
import com.community.api.like.dto.LikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/likes")
public class LikeController {
    private final Likeservice likeservice;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> pushLike(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long postId) {
        LikeResponse likeResponse = likeservice.pushLike(principal.getUserId(), postId);
        return ResponseEntity.ok(ApiResponse.of("like_success", likeResponse));
    }
}
