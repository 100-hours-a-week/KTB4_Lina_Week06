package com.community.api.comment;

import com.community.api.comment.dto.CreateCommentRequest;
import com.community.api.common.BadRequestException;
import com.community.api.common.ForbiddenException;
import com.community.api.post.Post;
import com.community.api.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 작성
    public Long createComment(Long userId, Long postId, CreateCommentRequest request){
        // 댓글 내용 필수 작성
        if (request.getContent() == null || request.getContent().isBlank())
        throw new BadRequestException("content_required");

        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("not_found_post"));

        // 댓글 저장
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setAuthorId(userId);
        comment.setPostId(postId);

        Comment saved = commentRepository.save(comment);

        // 댓글 수 갱신
        post.setCommentsCount(post.getCommentsCount() + 1);
        postRepository.save(post);

        return saved.getCommentId();
    }

    // 댓글 수정
    public Long updateComment(Long userId, Long commentId, CreateCommentRequest request){
        // 댓글 내용 필수 작성
        if (request.getContent() == null || request.getContent().isBlank())
            throw new BadRequestException("content_required");

        // 댓글 찾기
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("not_found_comment"));

        // 본인 댓글 인증
        if (!comment.getAuthorId().equals(userId)){
            throw new ForbiddenException("forbidden");
        }

        comment.setContent(request.getContent());
        commentRepository.save(comment);
        return comment.getCommentId();
    }

    // 댓글 삭제
    public void deleteComment(Long userId, Long commentId){

        // 댓글 찾기
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("not_found_comment"));

        // 본인 댓글 인증
        if (!comment.getAuthorId().equals(userId)){
            throw new ForbiddenException("forbidden");
        }

        commentRepository.deleteById(commentId);

        // 댓글 수 갱신
        postRepository.findById(comment.getPostId()).ifPresent(post -> {
            post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
            postRepository.save(post);
        });
    }
}
