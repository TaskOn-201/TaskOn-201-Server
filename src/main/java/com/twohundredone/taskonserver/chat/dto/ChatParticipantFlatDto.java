package com.twohundredone.taskonserver.chat.dto;

public record ChatParticipantFlatDto(
        Long chatRoomId,
        Long userId,
        String name,
        String profileImageUrl
) {}
