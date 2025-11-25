package com.twohundredone.taskonserver.project.service;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.dto.ProjectCreateRequest;
import com.twohundredone.taskonserver.project.dto.ProjectCreateResponse;
import com.twohundredone.taskonserver.project.dto.ProjectSelectResponse;
import com.twohundredone.taskonserver.project.dto.TaskListResponse;
import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.entity.ProjectMember;
import com.twohundredone.taskonserver.project.enums.Role;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.project.repository.ProjectRepository;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public ProjectCreateResponse createProject(ProjectCreateRequest projectCreateRequest, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        User creator = userRepository.findById(userId).orElseThrow(() -> new CustomException(ResponseStatusError.UNAUTHORIZED));

        Project project = Project.builder()
                .projectName(projectCreateRequest.projectName())
                .projectDescription(projectCreateRequest.projectDescription())
                .build();
        project.addLeader(creator);

        Project savedProject = projectRepository.save(project);

        ProjectMember projectMember = ProjectMember.builder().project(savedProject).user(creator).role(Role.LEADER).build();
        projectMemberRepository.save(projectMember);

        return ProjectCreateResponse.builder()
                .projectId(project.getProjectId())
                .projectName(savedProject.getProjectName())
                .projectDescription(savedProject.getProjectDescription())
                .build();
    }

    public ProjectSelectResponse selectProject(Long projectId, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return projectRepository.findProjectWithMemberRole(projectId, userId);
    }

    public List<TaskListResponse> getProject(CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return projectRepository.findProjectListByUserId(userId);
    }
}
