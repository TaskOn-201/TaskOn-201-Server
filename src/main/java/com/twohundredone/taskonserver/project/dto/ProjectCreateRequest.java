package com.twohundredone.taskonserver.project.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectCreateRequest(
    @NotBlank
    String projectName,
    String projectDescription
) {

}
