package com.twohundredone.taskonserver.comment.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record CommentListResponse(
        Long commentId,
        CommentAuthorResponse author,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
