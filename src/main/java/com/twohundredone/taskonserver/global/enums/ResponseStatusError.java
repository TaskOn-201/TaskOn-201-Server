package com.twohundredone.taskonserver.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatusError {

    //400 Bad Request
    EMAIL_ALREADY_EXISTS(400, "이미 사용 중인 이메일입니다."),
    PASSWORD_INCORRECT_MISMATCH(400, "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    EMAIL_INVALID(400, "잘못된 이메일 형식입니다."),
    VALIDATION_ERROR(400, "입력값 검증 실패"),
    REFRESH_TOKEN_NOT_FOUND(400, "리프레시 토큰이 존재하지 않습니다."),
    FILE_EMPTY(400, "파일이 비어 있습니다."),
    FILE_TOO_LARGE(400, "파일 용량은 10MB 이하여야 합니다."),
    UNSUPPORTED_FILE_EXTENSION(400, "지원하지 않는 파일 형식입니다."),

    //401 Unauthorized
    PASSWORD_INCORRECT(401, "비밀번호를 확인해주세요."),
    INVALID_REFRESH_TOKEN(401, "유효하지 않은 리프레시 토큰입니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
    UNAUTHORIZED(401, "인증되지 않은 사용자입니다."),

    //403 Forbidden
    FORBIDDEN(403, "접근 권한이 없습니다."),

    //404 Not Found
    USER_NOT_FOUND(404, "사용자 정보를 찾을 수 없습니다."),
    PROJECT_NOT_FOUND(404, "프로젝트 정보를 찾을 수 없습니다."),
    READER_NOT_FOUND(404, "리더를 찾을 수 없습니다"),


    //500 Internal Server Error
    SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    FILE_UPLOAD_ERROR(500, "파일 업로드 중 오류가 발생했습니다.");

    private final int statusCode;
    private final String message;
}
