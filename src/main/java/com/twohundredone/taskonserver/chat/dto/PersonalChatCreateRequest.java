package com.twohundredone.taskonserver.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PersonalChatCreateRequest {

    @NotNull(message = "targetUserId는 필수입니다.")
    private Long targetUserId;
}
