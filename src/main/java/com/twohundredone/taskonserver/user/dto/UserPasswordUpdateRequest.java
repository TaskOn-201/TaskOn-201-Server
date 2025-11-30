package com.twohundredone.taskonserver.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserPasswordUpdateRequest(
        @NotBlank(message = "현재 비밀번호를 입력해주세요.") String currentPassword,
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=\\-`]).{8,15}$",
                message = "비밀번호는 영문 대문자, 소문자, 숫자, 특수기호가 각 1개 이상 포함된 8~15자 비밀번호여야 합니다."
        )
        @NotBlank(message = "새로운 비밀번호를 입력해주세요.") String newPassword,
        @NotBlank(message = "새로운 비밀번호를 입력해주세요.") String newPasswordConfirm
) {

}
