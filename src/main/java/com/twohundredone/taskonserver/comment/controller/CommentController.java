package com.twohundredone.taskonserver.comment.controller;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.comment.dto.CommentCreateRequest;
import com.twohundredone.taskonserver.comment.dto.CommentCreateResponse;
import com.twohundredone.taskonserver.comment.service.CommentService;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 생성", description = "댓글 생성 관련 API")
    @SecurityRequirement(name = "Authorization")
    @PostMapping
    public ResponseEntity<ApiResponse<CommentCreateResponse>> createComment
            (@RequestBody CommentCreateRequest request,
            @PathVariable Long projectId, @PathVariable Long taskId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        CommentCreateResponse response = commentService.createComment(request, projectId, taskId, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ResponseStatusSuccess.COMMENT_CREATE, response));
    }
}
