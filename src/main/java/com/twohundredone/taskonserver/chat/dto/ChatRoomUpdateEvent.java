package com.twohundredone.taskonserver.chat.dto;

import lombok.Builder;

@Builder
public record ChatRoomUpdateEvent(
        Long chatRoomId,
        String lastMessage,
        String lastMessageTime
) {

}
