package com.community.api.post;

import com.community.api.common.ApiResponse;
import com.community.api.post.dto.CreatePostRequest;
import com.community.api.post.dto.PostDetailResponse;
import com.community.api.post.dto.PostListResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<?>> posts(HttpServletRequest httpRequest, @RequestBody CreatePostRequest request) throws Exception{
        Long userId = (Long) httpRequest.getAttribute("userId");
        Long postId = postService.createPost(userId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.of("create_post_success", Map.of("post_id", postId)));

    }

    // 게시글 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPosts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) throws IOException{
        PostListResponse response = postService.getPosts(page, limit);
        return ResponseEntity.status(200)
                .body(ApiResponse.of("get_posts_success", response));
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> getDetailPost(@PathVariable Long postId) throws Exception{
        PostDetailResponse response = postService.getDetailPost(postId);
        return ResponseEntity.status(200)
                .body(ApiResponse.of("get_post_success", response));
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> updatePost(HttpServletRequest httpRequest, @PathVariable Long postId, @RequestBody CreatePostRequest request) throws IOException {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Long updatedPostId = postService.updatePost(userId, postId, request);
        return ResponseEntity.status(200)
                .body(ApiResponse.of("update_post_success", Map.of("post_id", updatedPostId)));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<?>> deletePost(HttpServletRequest httpRequest, @PathVariable Long postId) throws IOException{
        Long userId = (Long) httpRequest.getAttribute("userId");
        postService.deletePost(userId, postId);
        return ResponseEntity.status(204).build();
    }
}
