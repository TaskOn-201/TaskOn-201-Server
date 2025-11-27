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
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
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
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(ResponseStatusError.UNAUTHORIZED));
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByUser_UserId(userId);
        ProjectMember projectMember = projectMembers.stream().filter(pm -> pm.getProject().getProjectId().equals(projectId)).findFirst().orElseThrow(() -> new CustomException(ResponseStatusError.UNAUTHORIZED));
        return ProjectSelectResponse.builder().project(project).role(projectMember.getRole()).build();
    }

    public List<TaskListResponse> getProject(CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        List<TaskListResponse> taskListResponses = new ArrayList<>();
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByUser_UserId(userId);
        for(ProjectMember projectMember : projectMembers) {
            List<Project> projects = projectRepository.findAllByProjectId(projectMember.getProject().getProjectId());
            List<TaskListResponse> currentTaskResponse = projects.stream()
                    .map(project -> new TaskListResponse(project.getProjectId(), project.getProjectName(), projectMember.getRole())).toList();
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

    public List<ProjectMemberListResponse> getProjectMemberList(CustomUserDetails userDetails, Long projectId) {
        List<ProjectMemberListResponse> projectMemberListResponses = new ArrayList<>();
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProject_ProjectId(projectId);
        for(ProjectMember projectMember : projectMembers) {
            List<User> users = userRepository.findAllByUserId(projectMember.getUser().getUserId());
            List<ProjectMemberListResponse> currentprojectMemberList = users.stream()
                    .map(user -> new ProjectMemberListResponse(user.getUserId(), user.getName(), user.getEmail(), user.getProfileImageUrl(), projectMember.getRole()))
                    .toList();
            projectMemberListResponses.addAll(currentprojectMemberList);
        }
        return  projectMemberListResponses;
    }

    public ProjectSettingsResponseInfo ProjectSettingsResponseInfo(CustomUserDetails userDetails, Long projectId) {
        Long userId = userDetails.getId();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_NOT_FOUND));
        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProject_ProjectId(projectId);
        ProjectSettingsResponseInfo.Leader leader = projectMembers.stream().filter(pm -> pm.getRole().equals(Role.LEADER)).findFirst()
                .map(pm -> {
                    User user = pm.getUser();
                    return ProjectSettingsResponseInfo.Leader.builder().userId(user.getUserId())
                            .name(user.getName()).profileImageUrl(user.getProfileImageUrl()).build();
                }).orElseThrow(() -> new CustomException(ResponseStatusError.READER_NOT_FOUND));

        List<ProjectSettingsResponseInfo.Member> members = projectMembers.stream()
                .map(pm -> {
                    User user = pm.getUser();
                    return ProjectSettingsResponseInfo.Member.builder()
                            .userId(user.getUserId()).name(user.getName()).profileImageUrl(user.getProfileImageUrl()).build();
                }).toList();

        return ProjectSettingsResponseInfo.builder().projectId(projectId).projectName(project.getProjectName())
                .leader(leader).member(members).build();
    }


    public String deleteProject(CustomUserDetails userDetails, Long projectId, ProjectDeleteRequest projectDeleteRequest) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_NOT_FOUND));
        List<ProjectMember> projectMember = projectMemberRepository.findAllByProject_ProjectId(projectId);
        Boolean user = projectMember.stream().map(pm -> pm.getRole().equals(Role.LEADER)).findFirst().orElseThrow(() -> new CustomException(ResponseStatusError.USER_NOT_FOUND));

        if (projectDeleteRequest.projectName().equals(project.getProjectName()) || user)
        {
            projectRepository.deleteById(projectId);
        }
        return "";
    }
}
