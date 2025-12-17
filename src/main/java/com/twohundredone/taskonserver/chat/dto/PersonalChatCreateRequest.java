package com.twohundredone.taskonserver.chat.dto;

import jakarta.validation.constraints.NotNull;

public record PersonalChatCreateRequest(
        @NotNull(message = "targetUserId는 필수입니다.")
        Long targetUserId
) {}
