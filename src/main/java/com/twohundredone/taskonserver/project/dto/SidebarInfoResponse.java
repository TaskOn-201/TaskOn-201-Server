package com.twohundredone.taskonserver.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twohundredone.taskonserver.project.entity.Project;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SidebarInfoResponse {
    private ProjectInfo project;
    private List<OnlineUsersInfo> onlineUser;

    @Getter
    @Builder
    public static class ProjectInfo {
        private Long id;
        private String name;
    }

    @Getter
    @Builder
    public static class OnlineUsersInfo {
        private Long userId;
        private String name;
        private String profileImageUrl;
        private boolean isOnline;
    }

    //TODO
    Integer unreadChatCount;
}
