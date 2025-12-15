package com.twohundredone.taskonserver.chat.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListResponse {

    private Long chatRoomId;
    private String roomName;

    private List<Participant> participants;

    private String lastMessage;
    private String lastMessageTime; // "5분 전" or "251113T231154"
    private int unreadCount;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Participant {
        private Long userId;
        private String profileImageUrl;
    }
}
