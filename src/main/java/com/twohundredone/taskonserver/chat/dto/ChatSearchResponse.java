package com.twohundredone.taskonserver.chat.dto;

import com.twohundredone.taskonserver.chat.enums.ChatType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public record ChatSearchResponse(
        List<ChatRoomSearchItem> chatRooms
) {

    @Builder
    public record ChatRoomSearchItem(
            Long chatRoomId,
            String roomName,
            ChatType chatType,
            Long relatedTaskId,
            String lastMessage,
            LocalDateTime lastMessageAt,
            int unreadCount,
            List<ChatParticipantDto> participants
    ) {
        // QueryDSL 전용 생성자
        public ChatRoomSearchItem(
                Long chatRoomId,
                String roomName,
                ChatType chatType,
                Long relatedTaskId,
                String lastMessage,
                LocalDateTime lastMessageAt,
                int unreadCount
        ) {
            this(
                    chatRoomId,
                    roomName,
                    chatType,
                    relatedTaskId,
                    lastMessage,
                    lastMessageAt,
                    unreadCount,
                    List.of()   // 기본값
            );
        }
    }

    public record ChatParticipantDto(
            Long userId,
            String name,
            String profileImageUrl
    ) {}
}
