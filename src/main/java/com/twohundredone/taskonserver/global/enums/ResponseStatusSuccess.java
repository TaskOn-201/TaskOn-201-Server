package com.twohundredone.taskonserver.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatusSuccess {

    SIGNUP_SUCCESS(201, "회원가입 성공"),
    PROJECT_CREATE(201, "프로젝트 생성 완료"),
    PROJECT_SELECT(200, "프로젝트 선택 완료"),
    GET_PROJECT_LIST(200, "프로젝트 목록 조회 성공"),
    GET_SIDEBAR_INFO(200, "사이드바 정보 조회 성공"),
    GET_PROJECT_MEMBER(200, "프로젝트 팀원 조회 성공"),
    GET_PROJECT_SETTINGS(200, "프로젝트 설정 조회 성공"),
    LOGIN_SUCCESS(200, "로그인 성공"),
    SUCCESS_LOGOUT(200, "로그아웃 성공"),
    EMAIL_AVAILABLE(200, "사용 가능한 이메일입니다."),
    LOGOUT_SUCCESS(200, "로그아웃 완료"),
    TOKEN_REISSUE_SUCCESS(200, "토큰 재발급 성공");

    private final int statusCode;
    private final String message;
}
