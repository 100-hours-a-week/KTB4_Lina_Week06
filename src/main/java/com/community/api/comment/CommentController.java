package com.community.api.comment;

import com.community.api.auth.CustomUserDetails;
import com.community.api.comment.dto.CreateCommentRequest;
import com.community.api.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> comments(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long postId, @RequestBody CreateCommentRequest request) {
        Long commentId = commentService.createComment(principal.getUserId(), postId, request);
        return ResponseEntity.ok(ApiResponse.of("create_comment_success", Map.of("comment_id", commentId)));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<?>> updateComment(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long postId,@PathVariable Long commentId, @RequestBody CreateCommentRequest request) {
        Long updatedCommentId = commentService.updateComment(principal.getUserId(), commentId, request);
        return ResponseEntity.ok(ApiResponse.of("update_comment_success", Map.of("comment_id", updatedCommentId)));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<?>> deleteComment(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long commentId) {
        commentService.deleteComment(principal.getUserId(), commentId);
        return ResponseEntity.status(204).build();
    }
}
