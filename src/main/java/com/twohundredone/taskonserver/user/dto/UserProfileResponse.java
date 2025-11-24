package com.twohundredone.taskonserver.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
    private Long userId;
    private String email;
    private String name;
    private String profileImageUrl;
}
