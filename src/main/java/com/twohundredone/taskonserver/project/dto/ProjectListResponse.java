package com.twohundredone.taskonserver.project.dto;

import com.twohundredone.taskonserver.project.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class ProjectListResponse {
    private Long projectId;
    private String projectName;
    private Role role;
}

