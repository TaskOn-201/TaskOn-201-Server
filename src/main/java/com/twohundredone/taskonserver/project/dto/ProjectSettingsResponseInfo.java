package com.twohundredone.taskonserver.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class ProjectSettingsResponseInfo {
    private Long projectId;
    private String projectName;
    private Leader leader;
    private List<Member> member;

    @Getter
    @Builder
    public static class Leader {
        private Long userId;
        private String name;
        private String profileImageUrl;
    }

    @Getter
    @Builder
    public static class Member {
        private Long userId;
        private String name;
        private String profileImageUrl;
    }
}
