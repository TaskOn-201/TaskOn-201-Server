package com.twohundredone.taskonserver.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentListResponse {
    private Long commentId;
    private Author author;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Author{
        private Long userId;
        private String name;
        private String profileImageUrl;
    }
}
