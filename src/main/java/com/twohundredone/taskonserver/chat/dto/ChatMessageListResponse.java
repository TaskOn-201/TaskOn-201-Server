package com.twohundredone.taskonserver.chat.dto;

import lombok.Builder;

@Builder
public record ChatMessageListResponse(
        Long messageId,
        Sender sender,
        String content,
        String sentTime,
        String displayTime
) {
    @Builder
    public record Sender(
            Long userId,
            String name,
            String profileImageUrl
    ) {}
}
