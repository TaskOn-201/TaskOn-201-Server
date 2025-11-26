package com.twohundredone.taskonserver.project.service;

import com.twohundredone.taskonserver.auth.service.CustomUserDetails;
import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.dto.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<ProjectSelectResponse> projectInfo = projectRepository.findProjectWithMemberRole(projectId, userId);
        return projectInfo.orElseThrow(() -> new CustomException(ResponseStatusError.USER_NOT_FOUND));
    }

    public List<TaskListResponse> getProject(CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        List<TaskListResponse> taskListResponses = new ArrayList<>();
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByUser_UserId(userId);
        for(ProjectMember projectMember : projectMembers) {
            List<Project> projects = projectRepository.findAllByProjectId(projectMember.getProject().getProjectId());
            List<TaskListResponse> currentTaskResponse = projects.stream()
                    .map(project -> new TaskListResponse(project.getProjectId(), project.getProjectName(), projectMember.getRole())).collect(Collectors.toList());
            taskListResponses.addAll(currentTaskResponse);
        }

        return taskListResponses;
    }

    public SidebarInfoResponse getSidebarInfo(CustomUserDetails userDetails, Long projectId) {
        Project requestProject = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_NOT_FOUND));
        Long userId = userDetails.getId();
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ResponseStatusError.USER_NOT_FOUND));

        SidebarInfoResponse.ProjectInfo projectInfo = SidebarInfoResponse.ProjectInfo.builder().id(projectId).name(requestProject.getProjectName()).build();

        SidebarInfoResponse.OnlineUsersInfo onlineUsersInfo = SidebarInfoResponse.OnlineUsersInfo.builder()
                .userId(userId).name(user.getName()).profileImageUrl(user.getProfileImageUrl()).isOnline(true).build();

        List<SidebarInfoResponse.OnlineUsersInfo> onlineUsersInfoList = List.of(onlineUsersInfo);

        return SidebarInfoResponse.builder().project(projectInfo).onlineUser(onlineUsersInfoList).build();
    }
}
