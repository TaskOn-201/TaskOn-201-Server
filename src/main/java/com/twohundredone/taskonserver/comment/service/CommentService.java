package com.twohundredone.taskonserver.comment.service;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.comment.dto.CommentCreateRequest;
import com.twohundredone.taskonserver.comment.dto.CommentCreateResponse;
import com.twohundredone.taskonserver.comment.entity.Comment;
import com.twohundredone.taskonserver.comment.repository.CommentRepository;
import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.repository.TaskParticipantRepository;
import com.twohundredone.taskonserver.task.repository.TaskRepository;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskParticipantRepository taskParticipantRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public CommentCreateResponse createComment(CommentCreateRequest request, Long projectId, Long taskId, CustomUserDetails userDetails) {
        Long userId =  userDetails.getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ResponseStatusError.USER_NOT_FOUND));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new CustomException(ResponseStatusError.TASK_NOT_FOUND));
        //해당 프로젝트에 속한 멤버만
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_FORBIDDEN));

        //해당 task에 속한 멤버만
        taskParticipantRepository.findByTask_TaskIdAndUser_UserId(taskId, userId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.TASK_FORBIDDEN));

        Comment comment = Comment.builder()
                .content(request.content())
                .user(user)
                .task(task)
                .build();

        Comment saveComment = commentRepository.save(comment);

        CommentCreateResponse.Author author = CommentCreateResponse.Author.builder()
                .userId(saveComment.getUser().getUserId())
                .name(saveComment.getUser().getName())
                .profileImageUrl(saveComment.getUser().getProfileImageUrl())
                .build();

        return CommentCreateResponse.builder()
                .commentId(saveComment.getId())
                .taskId(taskId)
                .author(author)
                .content(saveComment.getContent())
                .createdAt(saveComment.getCreatedAt())
                .updatedAt(saveComment.getModifiedAt())
                .build();
    }
}
