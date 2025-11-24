package com.twohundredone.taskonserver.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Getter
@Builder
public class ProjectCreateResponse {
    @Id
    private String projectId;
    @NotBlank
    private String projectName;
    private String projectDescription;
}
