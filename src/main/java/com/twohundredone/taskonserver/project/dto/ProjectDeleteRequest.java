package com.twohundredone.taskonserver.project.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjectDeleteRequest(
        @NotBlank
        String projectName
) {
}
