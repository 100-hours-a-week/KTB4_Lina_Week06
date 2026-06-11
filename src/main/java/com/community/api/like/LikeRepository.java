package com.community.api.like;

import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class LikeRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String FILE_PATH = "src/main/resources/data/likes.json";

    //json 파일 읽기
    private List<Like> readAll() throws IOException{
        File file = new File(FILE_PATH);
        return objectMapper.readValue(file, new TypeReference<List<Like>>() {});
    }

    // 좋아요 여부 확인
    public Optional<Like> findByPostIdAndUserId(Long postId, Long userId) throws IOException{
        return readAll().stream()
                .filter(like -> like.getUserId().equals(userId) && like.getPostId().equals(postId))
                .findFirst();
    }

    // 좋아요 추가
    public void save(Like like) throws IOException{
        List<Like> likes = readAll();
        likes.add(like);
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, likes);
    }

    // 좋아요 취소
    public void delete(Long postId, Long userId) throws IOException{
        List<Like> likes = readAll();
        List<Like> deleted = likes.stream()
                .filter(l-> ! (l.getPostId().equals(postId) && l.getUserId().equals(userId)))
                .collect(Collectors.toList());
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, deleted);
    }

}