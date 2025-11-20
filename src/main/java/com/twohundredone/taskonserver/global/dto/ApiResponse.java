package com.twohundredone.taskonserver.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

    private final int statusCode;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> success(ResponseStatusSuccess status, T data) {
        return ApiResponse.<T>builder()
                .statusCode(status.getStatusCode())
                .message(status.getMessage())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> fail(ResponseStatusError status, T data) {
        return ApiResponse.<T>builder()
                .statusCode(status.getStatusCode())
                .message(status.getMessage())
                .data(data)
                .build();
    }
}
