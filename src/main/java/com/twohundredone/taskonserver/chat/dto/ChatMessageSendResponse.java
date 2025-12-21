package com.twohundredone.taskonserver.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatMessageSendResponse(
        Long messageId,
        Long chatRoomId,
        Long senderId,
        String content,
        String sentTime,
        LocalDateTime createdAt
) {}