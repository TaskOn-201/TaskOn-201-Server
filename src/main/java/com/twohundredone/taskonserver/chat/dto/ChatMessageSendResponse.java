package com.twohundredone.taskonserver.chat.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageSendResponse {

    private Long messageId;
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private String sentTime; // "251103T232643"
}
