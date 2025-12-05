package com.twohundredone.taskonserver.user.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMeResponse {

    private Long userId;
    private String email;
    private String name;
    private String profileImageUrl;
    private List<ProjectInfo> joinedProjects;

    @Getter
    @Builder
    public static class ProjectInfo {
        private Long projectId;
        private String projectName;
    }

}
