package com.community.api.comment;

import com.community.api.comment.dto.CreateCommentRequest;
import com.community.api.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<ApiResponse<?>> comments(HttpServletRequest httpRequest, @PathVariable Long postId, @RequestBody CreateCommentRequest request) throws IOException {
        Long userId = (Long) httpRequest.getAttribute("userId");
        Long commentId = commentService.createComment(userId, postId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.of("create_comment_success", Map.of("comment_id", commentId)));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<?>> updateComment(HttpServletRequest httpRequest, @PathVariable Long postId,@PathVariable Long commentId, @RequestBody CreateCommentRequest request) throws IOException{
        Long userId = (Long) httpRequest.getAttribute("userId");
        Long updatedCommentId = commentService.updateComment(userId, commentId, request);
        return ResponseEntity.status(200)
                .body(ApiResponse.of("update_comment_success", Map.of("comment_id", updatedCommentId)));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<?>> deleteComment(HttpServletRequest httpRequest, @PathVariable Long commentId) throws IOException{
        Long userId = (Long) httpRequest.getAttribute("userId");
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.status(204).build();
    }
}
