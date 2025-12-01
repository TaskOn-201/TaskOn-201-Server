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
        User creator = userRepository.findById(userDetails.getId()).orElseThrow(() -> new CustomException(ResponseStatusError.UNAUTHORIZED));

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
                .onlineUsers(onlineUsers).unreadChatCount(5).build();

        //TODO: 채팅 관련 서비스 로직 추가 예정
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberListResponse> getProjectMemberList(CustomUserDetails userDetails, Long projectId) {
        Long userId = userDetails.getId();

        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_FORBIDDEN));

        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProject_ProjectId(projectId);

        return projectMembers.stream().map(pm -> {
            User u = pm.getUser();
            return new ProjectMemberListResponse(
                    u.getUserId(),
                    u.getName(),
                    u.getEmail(),
                    u.getProfileImageUrl(),
                    pm.getRole()
            );
        }).toList();
    }

    @Transactional(readOnly = true)
    public ProjectSettingsResponseInfo ProjectSettingsResponseInfo(CustomUserDetails userDetails, Long projectId) {
        Long userId = userDetails.getId();

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_NOT_FOUND));

        projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_FORBIDDEN));

        List<ProjectMember> projectMembers = projectMemberRepository.findAllByProject_ProjectId(projectId);

        ProjectMember leaderMember = projectMembers.stream()
                .filter(pm -> pm.getRole().equals(Role.LEADER))
                .findFirst()
                .orElseThrow(() -> new CustomException(ResponseStatusError.LEADER_NOT_FOUND));

        User leaderUser = leaderMember.getUser();

        var leader = ProjectSettingsResponseInfo.Leader.builder()
                .userId(leaderUser.getUserId())
                .name(leaderUser.getName())
                .profileImageUrl(leaderUser.getProfileImageUrl())
                .build();

        List<ProjectSettingsResponseInfo.Member> members = projectMembers.stream()
                .filter(pm -> pm.getRole().equals(Role.MEMBER))
                .map(pm -> {
                    User user = pm.getUser();
                    return ProjectSettingsResponseInfo.Member.builder()
                            .userId(user.getUserId())
                            .name(user.getName())
                            .profileImageUrl(user.getProfileImageUrl())
                            .build();
                }).toList();

        return ProjectSettingsResponseInfo.builder()
                .projectId(projectId)
                .projectName(project.getProjectName())
                .leader(leader)
                .members(members)
                .build();
    }


    @Transactional
    public void deleteProject(CustomUserDetails userDetails, Long projectId, ProjectDeleteRequest request) {
        Long userId = userDetails.getId();
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_NOT_FOUND));

        ProjectMember projectMember = projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new CustomException(ResponseStatusError.PROJECT_FORBIDDEN));

        if(projectMember.getRole() != Role.LEADER){
            throw new CustomException(ResponseStatusError.LEADER_NOT_FOUND);
        }

        if(!project.getProjectName().equals(request.projectName())){
            throw new CustomException(ResponseStatusError.PROJECT_NAME_NOT_MATCH);
        }

        projectRepository.delete(project);
    }
}
