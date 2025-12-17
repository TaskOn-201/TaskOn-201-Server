package com.twohundredone.taskonserver.chat.dto;

import lombok.Builder;

@Builder
public record ChatMessageSendResponse(
        Long messageId,
        Long chatRoomId,
        Long senderId,
        String content,
        String sentTime
) {}