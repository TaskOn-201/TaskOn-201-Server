package com.twohundredone.taskonserver.comment.controller;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.COMMENT_CREATE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.COMMENT_DELETE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.COMMENT_UPDATE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.GET_COMMENT_LIST;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.comment.dto.*;
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
    public ResponseEntity<ApiResponse<CommentCreateResponse>> createComment(
            @Valid @RequestBody CommentCreateRequest request,
            @PathVariable Long projectId, @PathVariable Long taskId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        CommentCreateResponse response = commentService.createComment(request, projectId, taskId, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(COMMENT_CREATE, response));
    }

    @Operation(summary = "댓글 조회", description = "댓글 조회 관련 API")
    @SecurityRequirement(name = "Authorization")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentListResponse>>> getComment(
            @PathVariable Long projectId, @PathVariable Long taskId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<CommentListResponse> response = commentService.getComment(projectId, taskId, userDetails);
        return ResponseEntity.ok(ApiResponse.success(GET_COMMENT_LIST, response));
    }

    @Operation(summary = "댓글 수정", description = "댓글 수정 관련 API")
    @SecurityRequirement(name = "Authorization")
    @PatchMapping("{commentId}")
    public ResponseEntity<ApiResponse<CommentUpdateResponse>> updateComment(
            @PathVariable Long projectId, @PathVariable Long taskId, @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        CommentUpdateResponse response = commentService.updateComment(projectId, taskId, commentId, request, userDetails);
        return ResponseEntity.ok(ApiResponse.success(COMMENT_UPDATE, response));
    }

    @Operation(summary = "댓글 삭제", description = "댓글 삭제 관련 API")
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long projectId, @PathVariable Long taskId, @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        commentService.deleteComment(projectId, taskId, commentId, userDetails);
        return ResponseEntity.ok(ApiResponse.success(COMMENT_DELETE, null));
    }
}

