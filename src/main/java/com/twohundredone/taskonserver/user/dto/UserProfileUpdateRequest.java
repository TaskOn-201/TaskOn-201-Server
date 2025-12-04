package com.twohundredone.taskonserver.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Pattern(
                regexp = "^[가-힣a-zA-Z]{2,15}$",
                message = "이름은 한글 또는 영어로만 구성된 2~15자 이름이어야 합니다."
        )
        private String name;

        public void setName(String name) {
                this.name = name;
        }
}
