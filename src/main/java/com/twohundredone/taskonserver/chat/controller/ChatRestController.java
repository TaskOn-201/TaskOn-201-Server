package com.twohundredone.taskonserver.chat.controller;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.chat.dto.ChatMessageListResponse;
import com.twohundredone.taskonserver.chat.dto.ChatMessageRequest;
import com.twohundredone.taskonserver.chat.dto.ChatMessageSendResponse;
import com.twohundredone.taskonserver.chat.dto.ChatRoomListResponse;
import com.twohundredone.taskonserver.chat.service.ChatService;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor

public class ChatRestController {

    private final ChatService chatService;

    @Operation(
            summary = "채팅방 리스트 조회",
            description = "로그인한 사용자가 속한 모든 채팅방 목록을 조회합니다."
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/rooms")
    public ApiResponse<List<ChatRoomListResponse>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.success(
                ResponseStatusSuccess.CHAT_ROOM_LIST_SUCCESS,
                chatService.getMyChatRooms(userDetails.getId())
        );
    }

    // 채팅방 메시지 리스트 조회
    @Operation(summary = "채팅 메시지 리스트 조회", description = "특정 채팅방의 메시지 목록을 조회합니다.")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/rooms/{chatRoomId}/messages")
    public ApiResponse<List<ChatMessageListResponse>> getMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long chatRoomId
    ) {
        return ApiResponse.success(
                ResponseStatusSuccess.CHAT_MESSAGE_LIST_SUCCESS,
                chatService.getMessages(chatRoomId, userDetails.getId())
        );
    }

    // ✅ 채팅 메시지 전송(REST)
    @Operation(
            summary = "채팅 메시지 전송 완료",
            description = "특정 채팅방에 메시지를 전송합니다."
    )
    @PostMapping("/rooms/{chatRoomId}/messages")
    @SecurityRequirement(name = "Authorization")
    public ResponseEntity<ApiResponse<ChatMessageSendResponse>> sendMessageRest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long chatRoomId,
            @Valid @RequestBody ChatMessageRequest request
    ) {
        ChatMessageSendResponse saved =
                chatService.sendMessage(chatRoomId, userDetails.getId(), request);

        return ResponseEntity
                .status(201)
                .body(ApiResponse.success(ResponseStatusSuccess.CHAT_MESSAGE_SENT, saved));
    }
}
