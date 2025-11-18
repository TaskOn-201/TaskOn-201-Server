package com.twohundredone.taskonserver.auth.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
