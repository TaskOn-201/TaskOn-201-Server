package com.twohundredone.taskonserver.common.dto;

import java.util.List;
import org.springframework.data.domain.Slice;

public record SliceResponse<T>(
        List<T> content,
        int size,
        int number,
        boolean hasNext
) {

    public static <T> SliceResponse<T> from(Slice<T> slice) {
        return new SliceResponse<>(
                slice.getContent(),
                slice.getSize(),
                slice.getNumber(),
                slice.hasNext()
        );
    }
}
