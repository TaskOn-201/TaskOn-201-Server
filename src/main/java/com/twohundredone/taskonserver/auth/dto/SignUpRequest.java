package com.twohundredone.taskonserver.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
        @Pattern(
                regexp = "^[가-힣a-zA-Z]{2,15}$",
                message = "이름은 한글 또는 영어로만 구성된 2~15자 이름이어야 합니다."
        )
        @NotBlank String name,
        @NotBlank @CustomEmail String email,
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=\\-`]).{8,15}$",
                message = "비밀번호는 영문 대문자, 소문자, 숫자, 특수기호가 각 1개 이상 포함된 8~15자 비밀번호여야 합니다."
        )
        @NotBlank String password,
        @NotBlank  String passwordCheck
) {

}
