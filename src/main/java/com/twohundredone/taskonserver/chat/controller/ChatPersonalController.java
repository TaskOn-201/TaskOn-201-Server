package com.twohundredone.taskonserver.chat.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.SEARCH_USER_SUCCESS;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.chat.dto.PersonalChatCreateRequest;
import com.twohundredone.taskonserver.chat.dto.PersonalChatCreateResponse;
import com.twohundredone.taskonserver.chat.service.ChatService;
import com.twohundredone.taskonserver.common.dto.SliceResponse;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.user.dto.UserSearchResponse;
import com.twohundredone.taskonserver.user.service.UserSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/personal")
public class ChatPersonalController {

    private final ChatService chatService;
    private final UserSearchService userSearchService;

    @Operation(summary = "1:1 개인 채팅방 생성", description = "1:1 개인 채팅방 생성 API")
    @SecurityRequirement(name = "Authorization")
    @PostMapping
    public PersonalChatCreateResponse createOrGetPersonalChat(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PersonalChatCreateRequest request
    ) {
        Long myUserId = userDetails.getId();
        Long targetUserId = request.targetUserId();

        Long chatRoomId = chatService.createOrGetPersonalChatRoom(myUserId, targetUserId);

        return PersonalChatCreateResponse.builder()
                .chatRoomId(chatRoomId)
                .build();
    }

    @Operation(summary = "사용자 검색 - 채팅", description = "1:1 개인 채팅방 생성 전, 사용자 검색 API")
    @SecurityRequirement(name = "Authorization")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<SliceResponse<UserSearchResponse>>> searchForChat(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Slice<UserSearchResponse> result =
                userSearchService.searchForChat(
                        userDetails.getId(),
                        keyword,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        SEARCH_USER_SUCCESS,
                        SliceResponse.from(result)
                )
        );
    }
}
