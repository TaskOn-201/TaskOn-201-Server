package com.twohundredone.taskonserver.task.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.INVALID_DATE_RANGE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.NOT_PROJECT_MEMBER;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_FORBIDDEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_NOT_FOUND;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.USER_NOT_FOUND;

import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.project.repository.ProjectRepository;
import com.twohundredone.taskonserver.task.dto.TaskCreateRequest;
import com.twohundredone.taskonserver.task.dto.TaskCreateResponse;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.entity.TaskParticipant;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskRole;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import com.twohundredone.taskonserver.task.repository.TaskParticipantRepository;
import com.twohundredone.taskonserver.task.repository.TaskRepository;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final TaskParticipantRepository taskParticipantRepository;
    private final UserRepository userRepository;

    public TaskCreateResponse createTask(Long loginUserId, Long projectId, TaskCreateRequest request) {

        // 1) 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // 2) 로그인 유저가 프로젝트 멤버인지 확인
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, loginUserId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));

        // 3) Assignee(User) 조회
        User assignee = userRepository.findById(loginUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (request.startDate().isAfter(request.dueDate())) {
            throw new CustomException(INVALID_DATE_RANGE);
        }
        // 4) Task 생성
        Task task = Task.builder()
                .project(project)
                .taskTitle(request.title())
                .description(request.description())
                .status(request.status() != null ? request.status() : TaskStatus.TODO)
                .priority(request.priority() != null ? request.priority() : TaskPriority.LOW)
                .startDate(request.startDate())
                .dueDate(request.dueDate())
                .build();

        taskRepository.save(task);

        // === 5) TaskParticipants 생성 === //

        // 5-1) Assignee는 무조건 TaskParticipant로 추가
        TaskParticipant assigneeParticipant = TaskParticipant.builder()
                .task(task)
                .user(assignee)
                .taskRole(TaskRole.ASSIGNEE)
                .build();

        taskParticipantRepository.save(assigneeParticipant);

        List<Long> participantIds = request.participantIds() != null
                ? request.participantIds()
                : List.of();
        participantIds = participantIds.stream().distinct().toList();

        List<Long> savedParticipantIds = new ArrayList<>();

        // 5-2) participantIds에 있는 유저들을 추가
        for (Long participantId : participantIds) {

            // 이미 ASSIGNEE는 위에서 추가했으니 중복 방지
            if (participantId.equals(loginUserId))
                continue;

            // 프로젝트 멤버인지 확인
            projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, participantId)
                    .orElseThrow(() -> new CustomException(NOT_PROJECT_MEMBER));

            User participant = userRepository.findById(participantId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

            TaskParticipant taskParticipant = TaskParticipant.builder()
                    .task(task)
                    .user(participant)
                    .taskRole(TaskRole.PARTICIPANT)
                    .build();

            taskParticipantRepository.save(taskParticipant);
            savedParticipantIds.add(participantId);
        }

        return TaskCreateResponse.builder()
                .taskId(task.getTaskId())
                .projectId(projectId)
                .title(task.getTaskTitle())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assigneeId(loginUserId)
                .participantIds(savedParticipantIds)
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .description(task.getDescription())
                .build();
    }
}
