package com.twohundredone.taskonserver.chat.dto;

public record StompErrorResponse(
        String code,
        String message
) {}
