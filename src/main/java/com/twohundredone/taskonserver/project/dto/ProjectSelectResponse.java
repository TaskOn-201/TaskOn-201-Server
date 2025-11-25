package com.twohundredone.taskonserver.project.dto;

import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.entity.ProjectMember;
import com.twohundredone.taskonserver.project.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProjectSelectResponse {
    private Long id;
    private String projectName;
    private String projectDescription;
    private Role role;

    @Builder
    public ProjectSelectResponse(Project project, Role role){
        this.id = project.getProjectId();
        this.projectName = project.getProjectName();
        this.projectDescription = project.getProjectDescription();
        this.role = role;
    }
}
