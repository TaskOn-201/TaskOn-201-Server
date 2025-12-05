package com.twohundredone.taskonserver.global.util;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.FILE_EMPTY;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.FILE_TOO_LARGE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.UNSUPPORTED_FILE_EXTENSION;

import com.twohundredone.taskonserver.global.exception.CustomException;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

public final class FileValidator {
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024L; // 5MB
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp"
    );

    private FileValidator() {
    }

    public static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(FILE_EMPTY);
        }

        // 용량 체크
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new CustomException(FILE_TOO_LARGE);
        }

        // 확장자 체크
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new CustomException(UNSUPPORTED_FILE_EXTENSION);
        }

        String ext = originalFilename
                .substring(originalFilename.lastIndexOf('.') + 1)
                .toLowerCase();

        if (!ALLOWED_IMAGE_EXTENSIONS.contains(ext)) {
            throw new CustomException(UNSUPPORTED_FILE_EXTENSION);
        }
    }
}
