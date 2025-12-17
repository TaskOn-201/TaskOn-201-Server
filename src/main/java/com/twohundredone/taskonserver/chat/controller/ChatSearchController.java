package com.twohundredone.taskonserver.chat.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.CHAT_SEARCH_SUCCESS;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.chat.dto.ChatSearchResponse;
import com.twohundredone.taskonserver.chat.service.ChatSearchService;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@SecurityRequirement(name = "Authorization")
public class ChatSearchController {

    private final ChatSearchService chatSearchService;

    @GetMapping("/search")
    public ApiResponse<ChatSearchResponse> search(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.success(
                CHAT_SEARCH_SUCCESS,
                chatSearchService.search(userDetails.getId(), keyword)
        );
    }
}
