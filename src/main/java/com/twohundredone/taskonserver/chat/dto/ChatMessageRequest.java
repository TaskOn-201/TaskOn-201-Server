package com.twohundredone.taskonserver.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
        @NotBlank(message = "채팅 메시지는 비어 있을 수 없습니다.")
        String content
) {}
