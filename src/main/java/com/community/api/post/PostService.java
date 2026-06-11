package com.community.api.post;

import com.community.api.comment.Comment;
import com.community.api.comment.CommentRepository;
import com.community.api.comment.CommentService;
import com.community.api.common.BadRequestException;
import com.community.api.common.ForbiddenException;
import com.community.api.post.dto.AuthorInfo;
import com.community.api.post.dto.CreatePostRequest;
import com.community.api.post.dto.PostDetailResponse;
import com.community.api.post.dto.PostListResponse;
import com.community.api.user.User;
import com.community.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 게시글 작성
    public Long createPost(Long userId, CreatePostRequest request) throws IOException{
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
    public PostListResponse getPosts(int page, int limit) throws IOException{
        List<Post> posts = postRepository.findAll();
        int total = posts.size();
        int fromIndex = (page - 1)*limit;
        int toIndex = Math.min(fromIndex + limit, total);
        List<Post> paged = posts.subList(fromIndex, toIndex);
        boolean hasNext = (page * limit) < total;

        return PostListResponse.builder()
                .posts(paged)
                .page(page)
                .limit(limit)
                .total(total)
                .hasNext(hasNext)
                .build();
    }

    // 게시글 상세 조회
    public PostDetailResponse getDetailPost(Long postId) throws IOException{
        // 게시글이 존재하지 않을 때
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new BadRequestException("not_found_post"));

        User author = userRepository.findByUserId(post.getAuthorId()).get();
        //authorInfo 정보 반환
        AuthorInfo authorInfo = AuthorInfo.builder()
                .nickname(author.getNickname())
                .profileImage(author.getProfileImage())
                .build();

        List<Comment> comments = commentRepository.findByPostId(postId);

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
                .comments(comments)
                .build();
    }

    // 게시글 수정
    public Long updatePost(Long userId, Long postId, CreatePostRequest request) throws IOException{
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
        Post post = postRepository.findByPostId(postId)
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

        Post updated = postRepository.update(post);
        return updated.getPostId();
    }

    // 게시글 삭제
    public void deletePost(Long userId, Long postId) throws IOException{

        // 게시글 찾기
        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new BadRequestException("not_found_post"));

        // user 인증 -> 본인 게시글인지
        if (!post.getAuthorId().equals(userId)){
            throw new ForbiddenException("forbidden");
        }

        postRepository.delete(postId);
    }
}
