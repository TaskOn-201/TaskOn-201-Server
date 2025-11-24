package com.twohundredone.taskonserver.project.dto;

import com.twohundredone.taskonserver.project.enums.Role;

public class ProjectSelectResponse {
    private Long id;
    private String projectName;
    private String projectDescription;
    private String role;

    public ProjectSelectResponse(Long id, String projectName, String projectDescription, Role role) {
        this.id = id;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.role = role.name();
    }
}
