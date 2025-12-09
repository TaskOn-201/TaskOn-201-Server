package com.twohundredone.taskonserver.task.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.ASSIGNEE_NOT_FOUND;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.FORBIDDEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.INVALID_DATE_RANGE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.INVALID_PAST_DATE_CREATE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.INVALID_PAST_DATE_UPDATE;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.NOT_PROJECT_MEMBER;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_FORBIDDEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_NOT_FOUND;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.TASK_NOT_FOUND;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.TASK_PROJECT_MISMATCH;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.USER_NOT_FOUND;

import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.project.repository.ProjectRepository;
import com.twohundredone.taskonserver.task.dto.TaskBoardItemDto;
import com.twohundredone.taskonserver.task.dto.TaskBoardResponse;
import com.twohundredone.taskonserver.task.dto.TaskCreateRequest;
import com.twohundredone.taskonserver.task.dto.TaskCreateResponse;
import com.twohundredone.taskonserver.task.dto.TaskDetailResponse;
import com.twohundredone.taskonserver.task.dto.TaskStatusUpdateRequest;
import com.twohundredone.taskonserver.task.dto.TaskStatusUpdateResponse;
import com.twohundredone.taskonserver.task.dto.TaskUpdateRequest;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.entity.TaskParticipant;
import com.twohundredone.taskonserver.task.enums.TaskPriority;
import com.twohundredone.taskonserver.task.enums.TaskRole;
import com.twohundredone.taskonserver.task.enums.TaskStatus;
import com.twohundredone.taskonserver.task.repository.TaskParticipantRepository;
import com.twohundredone.taskonserver.task.repository.TaskQueryRepository;
import com.twohundredone.taskonserver.task.repository.TaskRepository;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final TaskParticipantRepository taskParticipantRepository;
    private final UserRepository userRepository;
    private final TaskQueryRepository taskQueryRepository;

    @Transactional
    public TaskCreateResponse createTask(Long loginUserId, Long projectId, TaskCreateRequest request) {

        // 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // 로그인 유저가 프로젝트 멤버인지 확인
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, loginUserId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));

        // Assignee(User) 조회
        User assignee = userRepository.findById(loginUserId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        LocalDate today = LocalDate.now();

        if (request.startDate().isBefore(today) || request.dueDate().isBefore(today)) {
            throw new CustomException(INVALID_PAST_DATE_CREATE);
        }

        if (request.startDate().isAfter(request.dueDate())) {
            throw new CustomException(INVALID_DATE_RANGE);
        }
        // Task 생성
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

        // === TaskParticipants 생성 === //

        // Assignee는 무조건 TaskParticipant로 추가
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

        // participantIds에 있는 유저들을 추가
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

    @Transactional(readOnly = true)
    public TaskDetailResponse getTaskDetail(Long loginUserId, Long projectId, Long taskId) {

        // 프로젝트 존재 여부
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // 로그인 사용자 프로젝트 권한 확인
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, loginUserId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));

        // Task 조회
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        // Task가 이 프로젝트에 속한 Task인지 검증
        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new CustomException(TASK_PROJECT_MISMATCH);
        }

        // TaskParticipant 목록 조회
        List<TaskParticipant> participants =
                taskParticipantRepository.findAllByTask_TaskId(taskId);

        // Assignee 찾기
        TaskParticipant assignee = participants.stream()
                .filter(tp -> tp.getTaskRole().isAssignee()) // enum에 isAssignee() 만들어두면 깔끔
                .findFirst()
                .orElseThrow(() -> new CustomException(ASSIGNEE_NOT_FOUND));

        TaskDetailResponse.AssigneeDto assigneeDto =
                TaskDetailResponse.AssigneeDto.builder()
                        .userId(assignee.getUser().getUserId())
                        .name(assignee.getUser().getName())
                        .profileImageUrl(assignee.getUser().getProfileImageUrl())
                        .build();

        // Participant DTO 변환 (assignee 제외)
        List<TaskDetailResponse.ParticipantDto> participantDtos =
                participants.stream()
                        .filter(tp -> tp.getTaskRole().isParticipant())
                        .map(tp -> TaskDetailResponse.ParticipantDto.builder()
                                .userId(tp.getUser().getUserId())
                                .name(tp.getUser().getName())
                                .profileImageUrl(tp.getUser().getProfileImageUrl())
                                .build())
                        .toList();

        // 최종 Response 변환
        return TaskDetailResponse.builder()
                .taskId(task.getTaskId())
                .projectId(projectId)
                .title(task.getTaskTitle())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assignee(assigneeDto)
                .participants(participantDtos)
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .description(task.getDescription())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getModifiedAt())
                .build();
    }

    @Transactional
    public TaskDetailResponse updateTask(Long loginUserId, Long projectId, Long taskId, TaskUpdateRequest request) {

        // 프로젝트 검증
        projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // 사용자가 프로젝트 멤버인지 확인
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, loginUserId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));

        // Task 조회
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        // Task가 이 프로젝트에 속했는지 확인
        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new CustomException(TASK_PROJECT_MISMATCH);
        }

        // TaskParticipant 조회
        List<TaskParticipant> participants =
                taskParticipantRepository.findAllByTask_TaskId(taskId);

        // Assignee 찾기
        TaskParticipant assignee = participants.stream()
                .filter(TaskParticipant::isAssignee)
                .findFirst()
                .orElseThrow(() -> new CustomException(ASSIGNEE_NOT_FOUND));

        // 로그인한 유저가 Assignee인지 확인
        if (!assignee.getUser().getUserId().equals(loginUserId)) {
            throw new CustomException(FORBIDDEN);  // 수정 권한 없음
        }

        // 날짜 유효성 검증
        LocalDate today = LocalDate.now();

        // dueDate 과거 금지
        if (request.dueDate().isBefore(today)) {
            throw new CustomException(INVALID_PAST_DATE_UPDATE);
        }

        // start > due 금지
        if (request.startDate().isAfter(request.dueDate())) {
            throw new CustomException(INVALID_DATE_RANGE);
        }


        // Task 자체 필드 업데이트
        task.updateTitle(request.title());
        task.updateDescription(request.description());
        task.updateStatus(request.status());
        task.updatePriority(request.priority());
        task.updateDates(request.startDate(), request.dueDate());

        // 참여자 목록 업데이트
        updateTaskParticipants(task, loginUserId, request.participantIds());

        // 업데이트 후 다시 참여자 조회
        List<TaskParticipant> updatedParticipants =
                taskParticipantRepository.findAllByTask_TaskId(taskId);

        // Assignee DTO
        TaskParticipant updatedAssignee = updatedParticipants.stream()
                .filter(TaskParticipant::isAssignee)
                .findFirst()
                .orElseThrow(() -> new CustomException(ASSIGNEE_NOT_FOUND));

        TaskDetailResponse.AssigneeDto assigneeDto =
                TaskDetailResponse.AssigneeDto.builder()
                        .userId(updatedAssignee.getUser().getUserId())
                        .name(updatedAssignee.getUser().getName())
                        .profileImageUrl(updatedAssignee.getUser().getProfileImageUrl())
                        .build();

        // Participant DTO 리스트
        List<TaskDetailResponse.ParticipantDto> participantDtos =
                updatedParticipants.stream()
                        .filter(TaskParticipant::isParticipant)
                        .map(tp -> TaskDetailResponse.ParticipantDto.builder()
                                .userId(tp.getUser().getUserId())
                                .name(tp.getUser().getName())
                                .profileImageUrl(tp.getUser().getProfileImageUrl())
                                .build())
                        .toList();

        // 최종 Response 반환
        return TaskDetailResponse.builder()
                .taskId(task.getTaskId())
                .projectId(projectId)
                .title(task.getTaskTitle())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assignee(assigneeDto)
                .participants(participantDtos)
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .description(task.getDescription())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getModifiedAt())
                .build();
    }

    @Transactional
    public void deleteTask(Long loginUserId, Long projectId, Long taskId) {

        // 프로젝트 검증
        projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // 프로젝트 멤버인지 확인
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, loginUserId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));

        // Task 조회
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        // Task가 이 프로젝트에 소속된 Task인지 확인
        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new CustomException(TASK_PROJECT_MISMATCH);
        }

        // TaskParticipant 조회
        List<TaskParticipant> participants =
                taskParticipantRepository.findAllByTask_TaskId(taskId);

        // 로그인한 유저가 Assignee인지 체크
        TaskParticipant assignee = participants.stream()
                .filter(TaskParticipant::isAssignee)
                .findFirst()
                .orElseThrow(() -> new CustomException(ASSIGNEE_NOT_FOUND));

        if (!assignee.getUser().getUserId().equals(loginUserId)) {
            throw new CustomException(FORBIDDEN);
        }

        // TaskParticipant 먼저 삭제
        taskParticipantRepository.deleteAllByTask_TaskId(taskId);

        // Task 삭제
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public TaskBoardResponse getTaskBoard(
            Long loginUserId,
            Long projectId,
            String title,
            TaskPriority priority,
            Long userId
    ) {

        // 프로젝트 권한 체크
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, loginUserId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));

        // Task 목록 조회
        List<TaskBoardItemDto> items =
                taskQueryRepository.findBoardItemsWithFilters(projectId, title, priority, userId);

        // 상태별로 분리
        return TaskBoardResponse.builder()
                .todo(filterByStatus(items, TaskStatus.TODO))
                .inProgress(filterByStatus(items, TaskStatus.IN_PROGRESS))
                .completed(filterByStatus(items, TaskStatus.COMPLETED))
                .build();
    }

    @Transactional
    public TaskStatusUpdateResponse updateTaskStatus(
            Long loginUserId,
            Long projectId,
            Long taskId,
            TaskStatusUpdateRequest request
    ) {

        // 프로젝트 검증
        projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        // 프로젝트 멤버인지 확인
        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, loginUserId)
                .orElseThrow(() -> new CustomException(PROJECT_FORBIDDEN));

        // Task 조회
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(TASK_NOT_FOUND));

        // Task가 이 프로젝트에 속하는지 검증
        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new CustomException(TASK_PROJECT_MISMATCH);
        }

        // TaskParticipant 조회 (Assignee 찾기)
        List<TaskParticipant> participants =
                taskParticipantRepository.findAllByTask_TaskId(taskId);

        TaskParticipant assignee = participants.stream()
                .filter(TaskParticipant::isAssignee)
                .findFirst()
                .orElseThrow(() -> new CustomException(ASSIGNEE_NOT_FOUND));

        // Assignee만 상태 변경 가능
        if (!assignee.getUser().getUserId().equals(loginUserId)) {
            throw new CustomException(FORBIDDEN);
        }

        // 상태 변경
        task.updateStatus(request.status());

        // 응답 반환
        return TaskStatusUpdateResponse.builder()
                .taskId(task.getTaskId())
                .projectId(projectId)
                .status(task.getStatus())
                .updatedAt(task.getModifiedAt())
                .build();
    }

    private void updateTaskParticipants(Task task, Long assigneeId, List<Long> newIds) {

        Long projectId = task.getProject().getProjectId();

        // 기존 참여자 전체 삭제
        taskParticipantRepository.deleteAllByTask_TaskId(task.getTaskId());

        // 중복 제거
        if (newIds == null) newIds = List.of();
        newIds = newIds.stream().distinct().toList();

        // Assignee 다시 추가
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        TaskParticipant assigneePart = TaskParticipant.builder()
                .task(task)
                .user(assignee)
                .taskRole(TaskRole.ASSIGNEE)
                .build();

        taskParticipantRepository.save(assigneePart);

        // 새로운 참여자 처리
        for (Long newId : newIds) {

            // assignee 중복 방지
            if (newId.equals(assigneeId)) continue;

            // 프로젝트 멤버인지 검증 추가
            projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, newId)
                    .orElseThrow(() -> new CustomException(NOT_PROJECT_MEMBER));

            User user = userRepository.findById(newId)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

            TaskParticipant taskParticipant = TaskParticipant.builder()
                    .task(task)
                    .user(user)
                    .taskRole(TaskRole.PARTICIPANT)
                    .build();

            taskParticipantRepository.save(taskParticipant);
        }
    }

    private List<TaskBoardItemDto> filterByStatus(
            List<TaskBoardItemDto> items, TaskStatus status
    ) {
        return items.stream()
                .filter(item -> item.status() == status)
                .toList();
    }

}
