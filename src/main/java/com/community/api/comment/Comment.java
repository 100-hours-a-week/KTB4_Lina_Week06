package com.community.api.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class Comment {
    private String content;
    private String createdAt = LocalDateTime.now().
            format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private Long authorId;
    private Long postId;
    private Long commentId;
}