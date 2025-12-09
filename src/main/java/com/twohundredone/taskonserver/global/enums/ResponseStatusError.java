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
    PROJECT_NAME_NOT_MATCH(400, "프로젝트명이 일치하지 않습니다."),
    NOT_PROJECT_MEMBER(400, "해당 사용자는 프로젝트 멤버가 아닙니다."),
    CANNOT_REMOVE_LEADER(400, "프로젝트 리더는 삭제할 수 없습니다."),
    INVALID_DATE_RANGE(400, "시작일은 마감일보다 이후일 수 없습니다."),
    TASK_PROJECT_MISMATCH(400, "해당 업무는 이 프로젝트에 속하지 않습니다."),
    INVALID_PAST_DATE_CREATE(400, "시작일과 마감일은 오늘보다 이전일 수 없습니다."),
    INVALID_PAST_DATE_UPDATE(400, "마감일은 오늘보다 이전일 수 없습니다."),

    //401 Unauthorized
    PASSWORD_INCORRECT(401, "비밀번호를 확인해주세요."),
    INVALID_REFRESH_TOKEN(401, "유효하지 않은 리프레시 토큰입니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
    UNAUTHORIZED(401, "인증되지 않은 사용자입니다."),

    //403 Forbidden
    FORBIDDEN(403, "접근 권한이 없습니다."),
    PROJECT_FORBIDDEN(403, "해당 프로젝트에 대한 접근 권한이 없습니다."),
    ONLY_LEADER_CAN_DELETE(403, "프로젝트 삭제는 리더만 가능합니다."),

    //404 Not Found
    USER_NOT_FOUND(404, "사용자 정보를 찾을 수 없습니다."),
    PROJECT_NOT_FOUND(404, "프로젝트 정보를 찾을 수 없습니다."),
    LEADER_NOT_FOUND(404, "리더를 찾을 수 없습니다"),
    TASK_NOT_FOUND(404, "업무를 찾을 수 없습니다."),


    //500 Internal Server Error
    SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    FILE_UPLOAD_ERROR(500, "파일 업로드 중 오류가 발생했습니다."),
    ASSIGNEE_NOT_FOUND(500, "업무의 담당자 정보가 존재하지 않습니다.");


    private final int statusCode;
    private final String message;
}
