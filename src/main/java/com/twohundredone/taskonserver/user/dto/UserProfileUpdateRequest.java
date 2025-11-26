package com.twohundredone.taskonserver.user.dto;

import jakarta.validation.constraints.Pattern;

public record UserProfileUpdateRequest(
        @Pattern(
                regexp = "^[가-힣a-zA-Z]{2,15}$",
                message = "이름은 한글 또는 영어로만 구성된 2~15자 이름이어야 합니다."
        )
        String name
) {

}
