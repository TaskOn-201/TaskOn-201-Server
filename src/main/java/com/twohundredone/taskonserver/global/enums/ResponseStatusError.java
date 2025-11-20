package com.twohundredone.taskonserver.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatusError {

    //400
    EMAIL_ALREADY_EXISTS(400, "이미 사용 중인 이메일입니다."),
    EMAIL_INVALID(400, "잘못된 이메일 형식입니다."),
    VALIDATION_ERROR(400, "입력값 검증 실패"),

    //401
    LOGIN_FAILED(401, "로그인 실패 - 이메일 또는 비밀번호를 확인하세요."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
    UNAUTHORIZED(401, "인증되지 않은 사용자입니다."),

    //403
    FORBIDDEN(403, "접근 권한이 없습니다."),

    //404
    USER_NOT_FOUND(404, "사용자 정보를 찾을 수 없습니다."),

    //500
    SERVER_ERROR(500, "서버 내부 오류가 발생했습니다.");

    private final int statusCode;
    private final String message;
}
