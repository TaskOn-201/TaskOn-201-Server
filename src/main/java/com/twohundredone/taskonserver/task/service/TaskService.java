package com.twohundredone.taskonserver.task.service;

import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.repository.ProjectRepository;
import com.twohundredone.taskonserver.task.dto.TaskCreateRequest;
import com.twohundredone.taskonserver.task.dto.TaskCreateResponse;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.repository.TaskRepository;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    /**
     * Task 생성 로직
     * @param projectId 어떤 프로젝트에 속한 Task 인지 확인
     * @param userId    로그인 된 유저 id (작성자)
     * @param request   생성 요청 DTO
     */
    public TaskCreateResponse createTask(Long projectId, Long userId, TaskCreateRequest request) {

        // 1) 프로젝트 존재 확인하기
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        // 2) 사용자(작성자) 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 3) Task 엔티티 생성
        Task task = Task.builder()
                .project(project)
                .taskTitle(request.title())
                .description(request.description())
                .status(request.status())
                .priority(request.priority())
                .startDate(LocalDate.now())
                .dueDate(request.dueDate())
                .build();

        // 4) 저장하기
        Task saved = taskRepository.save(task);

        // 5) 응답 DTO 변환
        return TaskCreateResponse.from(saved);
    }
}
