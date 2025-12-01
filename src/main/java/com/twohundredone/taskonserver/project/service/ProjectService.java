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
import com.twohundredone.taskonserver.user.service.OnlineStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final OnlineStatusService onlineStatusService;

    @Transactional
    public ProjectCreateResponse createProject(ProjectCreateRequest request, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        User creator = userRepository.findById(userId).orElseThrow(() -> new CustomException(ResponseStatusError.UNAUTHORIZED));

        Project project = Project.builder()
                .projectName(request.projectName())
                .projectDescription(request.projectDescription())
                .build();

        project.addLeader(creator);

        Project savedProject = projectRepository.save(project);

        return ProjectCreateResponse.builder()
                .projectId(savedProject.getProjectId())
                .projectName(savedProject.getProjectName())
                .projectDescription(savedProject.getProjectDescription())
                .build();
    }

    @Transactional(readOnly = true)
    public ProjectSelectResponse selectProject(Long projectId, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_NOT_FOUND));

        ProjectMember projectMember = projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_FORBIDDEN));

        return ProjectSelectResponse.builder().project(project).role(projectMember.getRole()).build();
    }

    @Transactional(readOnly = true)
    public List<ProjectListResponse> getProjectList(CustomUserDetails userDetails) {
        Long userId = userDetails.getId();

        List<ProjectMember> memberships = projectMemberRepository.findAllByUser_UserId(userId);

        return memberships.stream()
                .map(pm -> new ProjectListResponse(
                        pm.getProject().getProjectId(),
                        pm.getProject().getProjectName(),
                        pm.getRole()
                )).toList();
    }

    @Transactional(readOnly = true)
    public SidebarInfoResponse getSidebarInfo(CustomUserDetails userDetails, Long projectId) {
        Long userId = userDetails.getId();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_NOT_FOUND));

        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_FORBIDDEN));

        SidebarInfoResponse.ProjectInfo projectInfo = SidebarInfoResponse.ProjectInfo.builder()
                .projectId(projectId).projectName(project.getProjectName()).build();

        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProject_ProjectId(projectId);

        List<SidebarInfoResponse.OnlineUsersInfo> onlineUsers = projectMembers.stream()
                .map(pm -> {
                    User u = pm.getUser();
                    return SidebarInfoResponse.OnlineUsersInfo.builder()
                            .userId(u.getUserId())
                            .name(u.getName())
                            .profileImageUrl(u.getProfileImageUrl())
                            .isOnline(onlineStatusService.isOnline(userId)).build();
                }).toList();

        return SidebarInfoResponse.builder().project(projectInfo)
                .onlineUsers(onlineUsers).build();

        //TODO: 채팅 관련 서비스 로직 추가 예정
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
        return null;
    }
}
