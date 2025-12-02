package com.twohundredone.taskonserver.user.dto;

public record UserSearchResponse(
        Long userId,
        String name,
        String email,
        String profileImageUrl
) {

}
