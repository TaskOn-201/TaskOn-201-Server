package com.twohundredone.taskonserver.comment.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.COMMENT_NOT_FOUND;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.COMMENT_TASK_MISMATCH;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.ONLY_ASSIGNEE_OR_AUTHOR_CAN_UPDATE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_FORBIDDEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.TASK_FORBIDDEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.TASK_PROJECT_MISMATCH;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.comment.dto.*;
import com.twohundredone.taskonserver.comment.entity.Comment;
import com.twohundredone.taskonserver.comment.repository.CommentRepository;
import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.entity.TaskParticipant;
import com.twohundredone.taskonserver.task.enums.TaskRole;
import com.twohundredone.taskonserver.task.repository.TaskParticipantRepository;
import com.twohundredone.taskonserver.task.repository.TaskRepository;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskParticipantRepository taskParticipantRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public CommentCreateResponse createComment(CommentCreateRequest request, Long projectId, Long taskId, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.USER_NOT_FOUND));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.TASK_NOT_FOUND));

        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new CustomException(TASK_PROJECT_MISMATCH);
        }

        // 접근 권한 검증 (project + task)
        validateTaskAccess(projectId, taskId, userId);

        Comment comment = Comment.builder()
                .content(request.content())
                .user(user)
                .task(task)
                .build();

        Comment saved = commentRepository.save(comment);

        CommentAuthorResponse author = CommentAuthorResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .build();

        return CommentCreateResponse.builder()
                .commentId(saved.getId())
                .taskId(taskId)
                .author(author)
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getModifiedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CommentListResponse> getComment(Long projectId, Long taskId, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.TASK_NOT_FOUND));

        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new CustomException(TASK_PROJECT_MISMATCH);
        }

        validateProjectAccess(projectId, userId);

        List<Comment> commentList =
                commentRepository.findAllByTask_TaskIdOrderByCreatedAtAsc(taskId);

        return commentList.stream()
                .map(comment -> {
                    User u = comment.getUser();
                    return CommentListResponse.builder()
                            .commentId(comment.getId())
                            .author(
                                    u == null
                                            ? CommentAuthorResponse.builder()
                                            .userId(null)
                                            .name("탈퇴한 사용자")
                                            .profileImageUrl(null)
                                            .build()
                                            : CommentAuthorResponse.builder()
                                                    .userId(u.getUserId())
                                                    .name(u.getName())
                                                    .profileImageUrl(u.getProfileImageUrl())
                                                    .build()
                            )
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .updatedAt(comment.getModifiedAt())
                            .build();
                })
                .toList();
    }


    @Transactional
    public CommentUpdateResponse updateComment(Long projectId, Long taskId, Long commentId, CommentUpdateRequest request, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        TaskParticipant taskParticipant =
                validateTaskAccess(projectId, taskId, userId);

        Comment comment =
                validateCommentScope(projectId, taskId, commentId);

        validateCommentAuthority(comment, taskParticipant, userId);

        comment.updateContent(request.content());

        return CommentUpdateResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getModifiedAt())
                .build();
    }

    @Transactional
    public void deleteComment(Long projectId, Long taskId, Long commentId, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        TaskParticipant taskParticipant =
                validateTaskAccess(projectId, taskId, userId);

        Comment comment =
                validateCommentScope(projectId, taskId, commentId);

        validateCommentAuthority(comment, taskParticipant, userId);

        commentRepository.delete(comment);
    }

    // 공통 검증 메서드

    // 프로젝트 멤버 검증
    private void validateProjectAccess(Long projectId, Long userId) {
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));
    }

    // 프로젝트 + 태스크 접근 검증
    private TaskParticipant validateTaskAccess(
            Long projectId,
            Long taskId,
            Long userId
    ) {
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));

        return taskParticipantRepository.findByTask_TaskIdAndUser_UserId(taskId, userId)
                .orElseThrow(() -> new CustomException(TASK_FORBIDDEN));
    }

    // 댓글 소속 검증 메서드
    private Comment validateCommentScope(
            Long projectId,
            Long taskId,
            Long commentId
    ) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));

        if (!comment.getTask().getTaskId().equals(taskId)) {
            throw new CustomException(COMMENT_TASK_MISMATCH);
        }

        if (!comment.getTask().getProject().getProjectId().equals(projectId)) {
            throw new CustomException(TASK_PROJECT_MISMATCH);
        }

        return comment;
    }

    // 댓글 접근 권한 검증 메서드
    private void validateCommentAuthority(
            Comment comment,
            TaskParticipant taskParticipant,
            Long userId
    ) {
        boolean isAssignee = TaskRole.ASSIGNEE.equals(taskParticipant.getTaskRole());
        boolean isAuthor = comment.getUser().getUserId().equals(userId);

        if (!isAssignee && !isAuthor) {
            throw new CustomException(ONLY_ASSIGNEE_OR_AUTHOR_CAN_UPDATE);
        }
    }

}

