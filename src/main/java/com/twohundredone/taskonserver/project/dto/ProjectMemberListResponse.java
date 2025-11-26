package com.twohundredone.taskonserver.project.dto;

import com.twohundredone.taskonserver.project.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProjectMemberListResponse {
    private Long userId;
    private String name;
    private String email;
    private String profileImageUrl;
    private Role role;
}
