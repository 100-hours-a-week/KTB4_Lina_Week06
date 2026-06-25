package com.community.api.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String createdAt = LocalDateTime.now().
            format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "post_id")
    private Long postId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
}