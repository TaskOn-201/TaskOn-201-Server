package com.twohundredone.taskonserver.comment.dto;

import lombok.Builder;

@Builder
public record CommentAuthorResponse(
        Long userId,
        String name,
        String profileImageUrl
) {}
