package com.twohundredone.taskonserver.project.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.entity.ProjectMember;
import com.twohundredone.taskonserver.project.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({ "projectId", "projectName", "projectDescription", "myRole" })
public class ProjectSelectResponse {
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private Role myRole;

    @Builder
    public ProjectSelectResponse(Project project, Role role){
        this.projectId = project.getProjectId();
        this.projectName = project.getProjectName();
        this.projectDescription = project.getProjectDescription();
        this.myRole = role;
    }
}
