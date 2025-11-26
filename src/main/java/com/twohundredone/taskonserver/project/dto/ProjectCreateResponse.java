package com.twohundredone.taskonserver.project.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectCreateResponse {
    private Long projectId;
    private String projectName;
    private String projectDescription;
}
