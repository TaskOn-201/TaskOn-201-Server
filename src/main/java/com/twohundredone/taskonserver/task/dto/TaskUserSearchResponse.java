package com.twohundredone.taskonserver.task.dto;

public record TaskUserSearchResponse(
        Long userId,
        String nickname,
        String email,
        String profileImage
) {

}
