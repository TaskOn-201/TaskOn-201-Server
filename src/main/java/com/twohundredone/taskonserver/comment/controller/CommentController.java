package com.twohundredone.taskonserver.comment.controller;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.comment.dto.*;
import com.twohundredone.taskonserver.comment.service.CommentService;
import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "댓글 조회", description = "댓글 조회 관련 API")
    @SecurityRequirement(name = "Authorization")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentListResponse>>> getComment
            (@PathVariable Long projectId, @PathVariable Long taskId,
             @AuthenticationPrincipal CustomUserDetails userDetails){
        List<CommentListResponse> response = commentService.getComment(projectId, taskId, userDetails);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.GET_COMMENT_LIST, response));
    }

    @Operation(summary = "댓글 수정", description = "댓글 수정 관련 API")
    @SecurityRequirement(name = "Authorization")
    @PatchMapping("{commentId}")
    public ResponseEntity<ApiResponse<CommentUpdateResponse>> updateComment
            (@PathVariable Long projectId, @PathVariable Long taskId, @PathVariable Long commentId,
             @RequestBody CommentUpdateRequest request,
             @AuthenticationPrincipal CustomUserDetails userDetails){
        CommentUpdateResponse response = commentService.updateComment(projectId, taskId, commentId, request, userDetails);
        return ResponseEntity.ok(ApiResponse.success(ResponseStatusSuccess.COMMENT_UPDATE, response));
    }
}