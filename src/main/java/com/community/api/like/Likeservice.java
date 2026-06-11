package com.community.api.like;

import com.community.api.common.BadRequestException;
import com.community.api.like.dto.LikeResponse;
import com.community.api.post.Post;
import com.community.api.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Likeservice {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeResponse pushLike(Long userId, Long postId) throws IOException {
        Optional<Like> likes = likeRepository.findByPostIdAndUserId(postId, userId);

        Post post = postRepository.findByPostId(postId)
                .orElseThrow(() -> new BadRequestException("not_found_post"));

        if (likes.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(postId, userId);
            post.setLikesCount(post.getLikesCount() - 1);
        } else {
            // 좋아요 추가
            Like like = new Like();
            like.setPostId(postId);
            like.setUserId(userId);
            likeRepository.save(like);
            post.setLikesCount(post.getLikesCount() + 1);
        }

        Post updated = postRepository.update(post);
        return LikeResponse.builder()
                .likesCount(updated.getLikesCount())
                .liked(!likes.isPresent())
                .build();
    }
}
