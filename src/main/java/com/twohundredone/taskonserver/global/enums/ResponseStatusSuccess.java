package com.twohundredone.taskonserver.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatusSuccess {

    PROJECT_CREATE(201, "프로젝트 생성 완료"),
    PROJECT_SELECT(200, "프로젝트 선택 완료"),
    GET_PROJECT_LIST(200, "프로젝트 목록 조회 성공"),
    GET_SIDEBAR_INFO(200, "사이드바 정보 조회 성공"),
    GET_PROJECT_MEMBER(200, "프로젝트 팀원 조회 성공"),
    GET_PROJECT_SETTINGS(200, "프로젝트 설정 조회 성공"),
    DELETE_PROJECT(200, "프로젝트 삭제 완료"),
    SIGNUP_SUCCESS(201, "회원가입을 완료했습니다."),
    ADD_PROJECT_MEMBER_SUCCESS(201, "프로젝트에 팀원을 성공적으로 추가하였습니다."),
    LOGIN_SUCCESS(200, "로그인되었습니다."),
    SUCCESS_LOGOUT(200, "로그아웃되었습니다."),
    EMAIL_AVAILABLE(200, "사용 가능한 이메일입니다."),
    TOKEN_REISSUE_SUCCESS(200, "토큰 재발급에 성공했습니다."),
    MODIFY_USER_INFO_SUCCESS(200, "사용자 정보 변경이 성공적으로 완료되었습니다."),
    UPDATE_PASSWORD_SUCCESS(200, "비밀번호 변경이 성공적으로 완료되었습니다."),
    SEARCH_USER_SUCCESS(200, "검색어에 맞는 사용자가 검색되었습니다."),
    NO_NEW_TEAM_MEMBER(200, "추가할 팀원이 없습니다. 모두 이미 프로젝트 멤버입니다."),
    SELECTED_USER_SUCCESS(200, "선택된 사용자를 조회하였습니다."),
    DELETE_PROJECT_MEMBER_SUCCESS(200, "프로젝트 팀원 삭제 성공"),
    TASK_CREATE_SUCCESS(200, "Task 생성 완료");
    private final int statusCode;

    private final String message;
}
