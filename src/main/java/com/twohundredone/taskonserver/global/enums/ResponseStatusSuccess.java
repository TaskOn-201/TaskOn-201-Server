package com.twohundredone.taskonserver.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatusSuccess {

    SIGNUP_SUCCESS(201, "회원가입을 완료했습니다."),
    LOGIN_SUCCESS(200, "로그인되었습니다."),
    SUCCESS_LOGOUT(200, "로그아웃되었습니다."),
    EMAIL_AVAILABLE(200, "사용 가능한 이메일입니다."),
    TOKEN_REISSUE_SUCCESS(200, "토큰 재발급에 성공했습니다."),
    MODIFY_USER_INFO_SUCCESS(200, "사용자 정보 변경이 성공적으로 완료되었습니다."),
    UPDATE_PASSWORD_SUCCESS(200, "비밀번호 변경이 성공적으로 완료되었습니다.");

    private final int statusCode;
    private final String message;
}
