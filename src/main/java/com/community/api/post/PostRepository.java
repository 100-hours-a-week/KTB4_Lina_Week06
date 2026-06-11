package com.community.api.post;

import com.community.api.user.User;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PostRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String FILE_PATH = "src/main/resources/data/posts.json";

    // 파일 읽기
    private List<Post> readAll() throws IOException {
        File file = new File(FILE_PATH);
        return objectMapper.readValue(file, new TypeReference<List<Post>>() {});
    }

    // 목록 조회
    public List<Post> findAll() throws IOException{
        return readAll();
    }

    // 상세 조회
    public Optional<Post> findByPostId(Long postId) throws IOException{
        return readAll().stream()
                .filter(post -> post.getPostId().equals(postId))
                .findFirst();
    }

    // 게시글 작성
    public Post save(Post post) throws IOException{
        List<Post> posts = readAll();
        Long newId = posts.stream()
                .mapToLong(Post::getPostId)
                .max()
                .orElse(0L) + 1;
        post.setPostId(newId);
        posts.add(post);
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, posts);
        return post;
    }

    // 게시글 수정
    public Post update(Post post) throws IOException{
        List<Post> posts = readAll();
        List<Post> updated = posts.stream()
                .map(p -> p.getPostId().equals(post.getPostId()) ? post : p)
                .collect(Collectors.toList());
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, updated);
        return post;
    }

    // 게시글 삭제
    public void delete(Long postId) throws IOException{
        List<Post> posts = readAll();
        List<Post> deleted = posts.stream()
                .filter(p -> !p.getPostId().equals(postId))
                .collect(Collectors.toList());
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, deleted);
    }

}
