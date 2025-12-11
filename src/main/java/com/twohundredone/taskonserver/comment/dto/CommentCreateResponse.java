package com.twohundredone.taskonserver.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentCreateResponse {
    private Long commentId;
    private Long taskId;
    private Author author;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class Author {
        private Long userId;
        private String name;
        private String profileImageUrl;
    }
}
