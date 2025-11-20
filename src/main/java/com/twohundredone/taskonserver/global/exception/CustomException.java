package com.twohundredone.taskonserver.global.exception;

import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ResponseStatusError statusError;

    public CustomException(ResponseStatusError statusError) {
        super(statusError.getMessage());
        this.statusError = statusError;
    }

}
