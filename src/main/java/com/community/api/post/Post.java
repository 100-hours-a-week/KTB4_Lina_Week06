package com.community.api.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String image;

    private int likesCount;
    private int viewsCount;
    private int commentsCount;

    private String createdAt = LocalDateTime.now().
            format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    // DB에 author_id라는 아이디 명시
    @Column(name = "author_id")
    private Long authorId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
}