package com.twohundredone.taskonserver.chat.dto;

import lombok.Builder;

@Builder
public record PersonalChatCreateResponse(
        Long chatRoomId
) {}
