package com.twohundredone.taskonserver.comment.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommentCreateResponse(
        Long commentId,
        Long taskId,
        CommentAuthorResponse author,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
