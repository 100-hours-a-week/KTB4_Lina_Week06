package com.community.api.post;

import com.community.api.comment.Comment;
import com.community.api.comment.CommentRepository;
import com.community.api.comment.dto.CommentResponse;
import com.community.api.common.BadRequestException;
import com.community.api.common.ForbiddenException;
import com.community.api.like.LikeRepository;
import com.community.api.post.dto.*;
import com.community.api.user.User;
import com.community.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    // 게시글 작성
    public Long createPost(Long userId, CreatePostRequest request){
        // 제목 필수 기입
        if (request.getTitle() == null || request.getTitle().isBlank())
            throw new BadRequestException("title_required");

        // 제목 최대 26자
        if (request.getTitle().length() > 26)
            throw new BadRequestException("invalid_title_length");

        // 내용 필수 기입
        if (request.getContent() == null || request.getContent().isBlank())
            throw new BadRequestException("content_required");

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImage(request.getImage());
        post.setLikesCount(0);
        post.setViewsCount(0);
        post.setCommentsCount(0);
        post.setCreatedAt(post.getCreatedAt());
        post.setAuthorId(userId);

        Post saved = postRepository.save(post);
        return saved.getPostId();
    }

    // 게시글 목록 조회
    public PostListResponse getPosts(int page, int limit){
        List<Post> posts = postRepository.findAll();
        int total = posts.size();
        int fromIndex = (page - 1) * limit;
        List<Post> paged;
        if (fromIndex < 0 || fromIndex >= total) {
            paged = new ArrayList<>();
        } else {
            int toIndex = Math.min(fromIndex + limit, total);
            paged = posts.subList(fromIndex, toIndex);
        }
        boolean hasNext = (page * limit) < total;

        // 각 게시글에 작성자 정보 추가
        List<PostListItem> items = new ArrayList<>();
        for(Post post : paged){
            User author = userRepository.findById(post.getAuthorId()).orElse(null);
            items.add(PostListItem.builder()
                    .postId(post.getPostId())
                    .title(post.getTitle())
                    .image(post.getImage())
                    .likesCount(post.getLikesCount())
                    .viewsCount(post.getViewsCount())
                    .commentsCount(post.getCommentsCount())
                    .createdAt(post.getCreatedAt())
                    .author(AuthorInfo.from(author))
                    .build());
        }

        return PostListResponse.builder()
                .posts(items)
                .page(page)
                .limit(limit)
                .total(total)
                .hasNext(hasNext)
                .build();
    }

    // 게시글 상세 조회
    public PostDetailResponse getDetailPost(Long postId){
        // 게시글이 존재하지 않을 때
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("not_found_post"));

        // 조회수 갱신
        post.setViewsCount(post.getViewsCount() + 1);
        postRepository.save(post);

        // 작성자 조회 - 탈퇴한 경우 "알수없음" 처리
        User author = userRepository.findById(post.getAuthorId()).orElse(null);
        AuthorInfo authorInfo = AuthorInfo.from(author);

        List<Comment> comments = commentRepository.findByPostId(postId);

        // 각 댓글에 작성자 정보 붙이기
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment c : comments) {
            User commentAuthor = userRepository.findById(c.getAuthorId()).orElse(null);
            commentResponses.add(CommentResponse.builder()
                    .commentId(c.getCommentId())
                    .content(c.getContent())
                    .createdAt(c.getCreatedAt())
                    .author(AuthorInfo.from(commentAuthor))
                    .build());
        }

        return PostDetailResponse.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .image(post.getImage())
                .likesCount(post.getLikesCount())
                .viewsCount(post.getViewsCount())
                .commentsCount(post.getCommentsCount())
                .createdAt(post.getCreatedAt())
                .author(authorInfo)
                .comments(commentResponses)
                .build();
    }

    // 게시글 수정
    public Long updatePost(Long userId, Long postId, CreatePostRequest request){
        // 제목 필수 기입
        if (request.getTitle() == null || request.getTitle().isBlank())
            throw new BadRequestException("title_required");

        // 제목 최대 26자
        if (request.getTitle().length() > 26)
            throw new BadRequestException("invalid_title_length");

        // 내용 필수 기입
        if (request.getContent() == null || request.getContent().isBlank())
            throw new BadRequestException("content_required");

        // 게시글 찾기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("not_found_post"));

        // user 인증 -> 본인 게시글인지
        if (!post.getAuthorId().equals(userId)){
            throw new ForbiddenException("forbidden");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        // 기존 이미지 유지
        if(request.getImage() != null){
            post.setImage(request.getImage());
        }

        Post updated = postRepository.save(post);
        return updated.getPostId();
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long userId, Long postId){

        // 게시글 찾기
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BadRequestException("not_found_post"));

        // user 인증 -> 본인 게시글인지
        if (!post.getAuthorId().equals(userId)){
            throw new ForbiddenException("forbidden");
        }

        postRepository.deleteById(postId);
        commentRepository.deleteByPostId(postId);
        likeRepository.deleteByPostId(postId);
    }
}