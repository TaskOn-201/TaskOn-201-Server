package com.twohundredone.taskonserver.project.dto;

//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.twohundredone.taskonserver.project.entity
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SidebarInfoResponse {
    private ProjectInfo project;
    private List<OnlineUsersInfo> onlineUsers;

    @Getter
    @Builder
    public static class ProjectInfo {
        private Long projectId;
        private String projectName;
    }

    @Getter
    @Builder
    public static class OnlineUsersInfo {
        private Long userId;
        private String name;
        private String profileImageUrl;
        private boolean isOnline;
    }

    //TODO: 읽지 않은 채팅 수 축가 예정
    Integer unreadChatCount;
}
