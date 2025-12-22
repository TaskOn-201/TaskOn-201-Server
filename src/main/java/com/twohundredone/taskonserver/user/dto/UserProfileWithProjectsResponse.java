package com.twohundredone.taskonserver.user.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record UserProfileWithProjectsResponse(
        Long userId,
        String name,
        String email,
        String profileImageUrl,
        List<UserProjectResponse> projects
) {

    @Builder
    public record UserProjectResponse(
            Long projectId,
            String projectName
    ) {
    }
}
