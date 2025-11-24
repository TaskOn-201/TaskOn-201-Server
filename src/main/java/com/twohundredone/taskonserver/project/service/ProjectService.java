package com.twohundredone.taskonserver.project.service;

import com.twohundredone.taskonserver.project.dto.ProjectCreateRequest;
import com.twohundredone.taskonserver.project.dto.ProjectCreateResponse;
import com.twohundredone.taskonserver.project.dto.ProjectSelectResponse;
import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.repository.ProjectRepository;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectCreateResponse createProject(ProjectCreateRequest projectCreateRequest) {
        Project project = Project.builder()
                .projectName(projectCreateRequest.projectName())
                .projectDescription(projectCreateRequest.projectDescription())
                .build();

        Project savedProject = projectRepository.save(project);

        return ProjectCreateResponse.builder()
                .projectName(savedProject.getProjectName())
                .projectDescription(savedProject.getProjectDescription())
                .build();
    }

    public ProjectSelectResponse selectProject(Long projectId) {
        User user = new User();
        Long userId = user.getUserId();
        return projectRepository.findProjectWithMemberRole(projectId, userId);
    }
}
