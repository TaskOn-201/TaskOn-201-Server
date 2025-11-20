package com.twohundredone.taskonserver.common;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ResponseStatusError statusError;

    public CustomException(ResponseStatusError statusError) {
        super(statusError.getMessage());
        this.statusError = statusError;
    }

}
