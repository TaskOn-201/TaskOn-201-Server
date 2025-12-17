package com.twohundredone.taskonserver.chat.dto;


import java.util.List;
import lombok.Builder;


@Builder
public record ChatRoomListResponse(
        Long chatRoomId,
        String roomName,
        List<Participant> participants,
        String lastMessage,
        String lastMessageTime,
        int unreadCount
) {
    public record Participant(
            Long userId,
            String profileImageUrl
    ) {}
}

