package com.twohundredone.taskonserver.chat.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageListResponse {

    private Long messageId;      // 채팅 메시지 ID

    private Sender sender;       // 발신자 정보

    private String content;      // 메시지 내용

    private String sentTime;     // 예: 251103T232643 (원본 시간)
    private String displayTime;  // 예: "3분 전"

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Sender {

        private Long userId;             // 발신자 ID
        private String name;             // 발신자 이름
        private String profileImageUrl;  // 프로필 이미지 URL
    }
    }

