package com.twohundredone.taskonserver.project.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AddMemberRequest(
        @NotEmpty(message = "추가할 사용자 ID 목록은 비어있을 수 없습니다.")
        List<Long> userIds
) {

}
