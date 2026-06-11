package com.community.api.like;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Like {
    private Long postId;
    private Long userId;
}
