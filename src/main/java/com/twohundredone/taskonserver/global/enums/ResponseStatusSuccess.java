package com.twohundredone.taskonserver.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatusSuccess {

    SIGNUP_SUCCESS(201, "회원가입 성공"),
    LOGIN_SUCCESS(200, "로그인 성공"),
    SUCCESS_LOGOUT(200, "로그아웃 성공"),
    EMAIL_AVAILABLE(200, "사용 가능한 이메일입니다."),
    LOGOUT_SUCCESS(200, "로그아웃 완료"),
    TOKEN_REISSUE_SUCCESS(200, "토큰 재발급 성공"),
    MODIFY_USER_INFO_SUCCESS(200, "사용자 정보 수정 완료");

    private final int statusCode;
    private final String message;
}
