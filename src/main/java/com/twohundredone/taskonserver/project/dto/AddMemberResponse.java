package com.twohundredone.taskonserver.project.dto;

import java.util.List;

public record AddMemberResponse(
        List<Long> addedUserIds
) {

}
