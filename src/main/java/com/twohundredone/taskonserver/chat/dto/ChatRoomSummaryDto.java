package com.twohundredone.taskonserver.chat.dto;

import java.time.LocalDateTime;

public record ChatRoomSummaryDto(
        Long chatRoomId,
        String roomName,
        String lastMessage,
        LocalDateTime lastMessageAt,
        int unreadCount
) {}
