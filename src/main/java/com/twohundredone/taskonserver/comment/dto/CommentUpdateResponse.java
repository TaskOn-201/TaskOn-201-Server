package com.twohundredone.taskonserver.comment.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record CommentUpdateResponse(
        Long commentId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
