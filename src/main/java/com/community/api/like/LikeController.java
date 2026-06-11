package com.community.api.like;

import com.community.api.common.ApiResponse;
import com.community.api.like.dto.LikeResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/likes")
public class LikeController {
    private final Likeservice likeservice;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> pushLike(HttpServletRequest httpRequest, @PathVariable Long postId) throws IOException{
        Long userId = (Long) httpRequest.getAttribute("userId");
        LikeResponse likeResponse = likeservice.pushLike(userId, postId);
        return ResponseEntity.status(200)
                .body(ApiResponse.of("like_success", likeResponse));
    }
}
