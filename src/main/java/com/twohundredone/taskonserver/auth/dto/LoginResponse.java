package com.twohundredone.taskonserver.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private UserInfo user;

    @Getter
    @Builder
    public static class UserInfo {
        private Long userId;
        private String email;
        private String name;
        private String profileImageUrl;
    }
}
