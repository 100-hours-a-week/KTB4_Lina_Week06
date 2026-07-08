package com.community.api.post;

import com.community.api.auth.CustomUserDetails;
import com.community.api.common.ApiResponse;
import com.community.api.post.dto.CreatePostRequest;
import com.community.api.post.dto.PostDetailResponse;
import com.community.api.post.dto.PostListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> posts(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody CreatePostRequest request) {
        Long postId = postService.createPost(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.of("create_post_success", Map.of("post_id", postId)));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPosts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) {
        PostListResponse response = postService.getPosts(page, limit);
        return ResponseEntity.status(200)
                .body(ApiResponse.of("get_posts_success", response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> getDetailPost(@PathVariable Long postId) {
        PostDetailResponse response = postService.getDetailPost(postId);
        return ResponseEntity.status(200)
                .body(ApiResponse.of("get_post_success", response));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> updatePost(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long postId, @RequestBody CreatePostRequest request) {
        Long updatedPostId = postService.updatePost(principal.getUserId(), postId, request);
        return ResponseEntity.ok(ApiResponse.of("update_post_success", Map.of("post_id", updatedPostId)));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long postId) {
        postService.deletePost(principal.getUserId(), postId);
        return ResponseEntity.status(204).build();
    }
}
