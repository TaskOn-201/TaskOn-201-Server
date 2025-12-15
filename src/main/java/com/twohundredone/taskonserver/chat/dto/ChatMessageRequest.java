package com.twohundredone.taskonserver.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {

    @NotBlank(message = "채팅 메시지는 비어 있을 수 없습니다.")
    private Long chatId;
    private String content;
}
