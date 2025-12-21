package com.twohundredone.taskonserver.chat.dto;


import com.twohundredone.taskonserver.chat.enums.ChatType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;


@Builder
public record ChatRoomListResponse(
        Long chatRoomId,
        String roomName,
        ChatType chatType,
        List<Participant> participants,
        String lastMessage,
        String lastMessageTime,
        LocalDateTime lastMessageAt,
        int unreadCount
) {
    public record Participant(
            Long userId,
            String name,
            String profileImageUrl
    ) {}
}

