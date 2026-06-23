package com.community.api.comment;

import com.community.api.post.Post;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CommentRepository {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String FILE_PATH = "src/main/resources/data/comments.json";

    // 파일 읽기
    private List<Comment> readAll() throws IOException {
        File file = new File(FILE_PATH);
        return objectMapper.readValue(file, new TypeReference<List<Comment>>() {});
    }

    // 댓글 찾기
    public Optional<Comment> findByCommentId(Long commentId) throws IOException {
        return readAll().stream()
                .filter(comment -> comment.getCommentId().equals(commentId))
                .findFirst();
    }

    // 게시글 댓글 목록
    public List<Comment> findByPostId(Long postId) throws IOException{
        return readAll().stream()
                .filter(comment -> comment.getPostId().equals(postId))
                .collect(Collectors.toList());
    }

    // 댓글 등록
    public Comment save(Comment comment) throws IOException{
        List<Comment> comments = readAll();
        Long newId = comments.stream()
                .mapToLong(Comment::getCommentId)
                .max()
                .orElse(0L) + 1;
        comment.setCommentId(newId);
        comments.add(comment);
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, comments);
        return comment;
    }

    // 댓글 수정
    public Comment update(Comment comment) throws IOException{
        List<Comment> comments = readAll();
        List<Comment> updated = comments.stream()
                .map(c -> c.getCommentId().equals(comment.getCommentId()) ? comment : c)
                .collect(Collectors.toList());
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, updated);
        return comment;
    }

    // 댓글 삭제
    public void delete(Long commentId) throws IOException{
        List<Comment> comments = readAll();
        List<Comment> deleted = comments.stream()
                .filter(c -> !c.getCommentId().equals(commentId))
                .collect(Collectors.toList());
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, deleted);
    }

    // 게시글 삭제 시 연관 댓글 일괄 삭제
    public void deleteByPostId(Long postId) throws IOException {
        List<Comment> comments = readAll();
        List<Comment> deleted = comments.stream()
                .filter(c -> !c.getPostId().equals(postId))
                .collect(Collectors.toList());
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, deleted);
    }
}
