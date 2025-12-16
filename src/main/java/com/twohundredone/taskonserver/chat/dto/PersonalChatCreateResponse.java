package com.twohundredone.taskonserver.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PersonalChatCreateResponse {
    private Long chatRoomId;
}
