package com.twohundredone.taskonserver.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Getter
@Builder
public class ProjectCreateResponse {
    @Id
    private Long projectId;
    @NotBlank
    private String projectName;
    private String projectDescription;
}
