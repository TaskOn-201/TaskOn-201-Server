package com.twohundredone.taskonserver.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {

}
