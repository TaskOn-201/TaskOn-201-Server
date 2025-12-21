package com.twohundredone.taskonserver.chat.dto;

import com.twohundredone.taskonserver.chat.enums.ChatType;
import java.time.LocalDateTime;

public record ChatRoomSummaryDto(
        Long chatRoomId,
        String roomName,
        ChatType chatType,
        String lastMessage,
        LocalDateTime lastMessageAt,  // 원본
        int unreadCount
) {}
