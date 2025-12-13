package com.twohundredone.taskonserver.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatusSuccess {

    //200
    PROJECT_SELECT(200, "프로젝트를 선택했습니다."),
    GET_PROJECT_LIST(200, "프로젝트 목록을 가져왔습니다."),
    GET_SIDEBAR_INFO(200, "사이드바 정보를 조회했습니다."),
    GET_PROJECT_MEMBER(200, "프로젝트 팀원을 조회했습니다."),
    GET_PROJECT_SETTINGS(200, "프로젝트 설정을 가져왔습니다."),
    DELETE_PROJECT(200, "프로젝트 삭제를 완료했습니다."),
    LOGIN_SUCCESS(200, "로그인되었습니다."),
    SUCCESS_LOGOUT(200, "로그아웃되었습니다."),
    EMAIL_AVAILABLE(200, "사용 가능한 이메일입니다."),
    TOKEN_REISSUE_SUCCESS(200, "토큰 재발급에 성공했습니다."),
    MODIFY_MY_INFO_SUCCESS(200, "사용자 정보 변경이 성공적으로 완료되었습니다."),
    GET_MY_INFO(200, "사용자 정보를 조회하였습니다."),
    UPDATE_PASSWORD_SUCCESS(200, "비밀번호 변경이 성공적으로 완료되었습니다."),
    SEARCH_USER_SUCCESS(200, "검색어에 맞는 사용자가 검색되었습니다."),
    NO_NEW_TEAM_MEMBER(200, "추가할 팀원이 없습니다. 모두 이미 프로젝트 멤버입니다."),
    SELECTED_USER_SUCCESS(200, "선택된 사용자를 조회하였습니다."),
    DELETE_PROJECT_MEMBER_SUCCESS(200, "프로젝트 팀원 삭제 성공"),
    GET_TASK_DETAIL(200, "Task 상세보기를 조회했습니다."),
    TASK_UPDATE(200, "Task 수정을 완료했습니다."),
    TASK_DELETE(200, "Task 삭제를 완료했습니다."),
    GET_TASK_BOARD(200, "Task 보드를 조회했습니다."),
    UPDATED_TASK_STATUS(200, "Task 상태 변경이 완료되었습니다."),
    GET_COMMENT_LIST(200, "댓글 목록 조회 성공"),
    COMMENT_UPDATE(200, "댓글 수정 완료"),

    //201 Created
    SIGNUP_SUCCESS(201, "회원가입을 완료했습니다."),
    PROJECT_CREATE(201, "프로젝트 생성을 완료했습니다."),
    ADD_PROJECT_MEMBER_SUCCESS(201, "프로젝트에 팀원을 추가하였습니다."),
    TASK_CREATE(201, "업무 생성을 완료했습니다."),
    COMMENT_CREATE(201, "댓글 생성을 완료했습니다.");

    private final int statusCode;

    private final String message;
}
