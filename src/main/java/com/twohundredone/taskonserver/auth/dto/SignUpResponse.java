package com.twohundredone.taskonserver.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponse {
    private Long userId;
    private String email;
    private String name;
    private String profileImageUrl;
}
