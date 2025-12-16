package com.twohundredone.taskonserver.chat.controller;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.chat.dto.PersonalChatCreateRequest;
import com.twohundredone.taskonserver.chat.dto.PersonalChatCreateResponse;
import com.twohundredone.taskonserver.chat.service.ChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
@RequestMapping("/api/chat/personal")
public class ChatPersonalController {

    private final ChatService chatService;

    @PostMapping
    public PersonalChatCreateResponse createOrGetPersonalChat(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PersonalChatCreateRequest request
    ) {
        Long myUserId = userDetails.getId();
        Long targetUserId = request.getTargetUserId();

        Long chatRoomId = chatService.createOrGetPersonalChatRoom(myUserId, targetUserId);

        return PersonalChatCreateResponse.builder()
                .chatRoomId(chatRoomId)
                .build();
    }
}
