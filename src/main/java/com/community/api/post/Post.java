package com.community.api.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class Post {
    private String title;
    private String content;
    private String image;
    private int likesCount;
    private int viewsCount;
    private int commentsCount;
    private String createdAt = LocalDateTime.now().
            format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private Long authorId;
    private Long postId;
}