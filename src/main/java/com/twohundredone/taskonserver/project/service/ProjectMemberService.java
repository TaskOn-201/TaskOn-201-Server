package com.twohundredone.taskonserver.project.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.CANNOT_REMOVE_LEADER;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.FORBIDDEN;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.NOT_PROJECT_MEMBER;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.PROJECT_NOT_FOUND;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.ADD_PROJECT_MEMBER_SUCCESS;
import static com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess.NO_NEW_TEAM_MEMBER;

import com.twohundredone.taskonserver.global.dto.ApiResponse;
import com.twohundredone.taskonserver.global.enums.ResponseStatusError;
import com.twohundredone.taskonserver.global.enums.ResponseStatusSuccess;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.dto.AddMemberRequest;
import com.twohundredone.taskonserver.project.dto.AddMemberResponse;
import com.twohundredone.taskonserver.project.entity.Project;
import com.twohundredone.taskonserver.project.entity.ProjectMember;
import com.twohundredone.taskonserver.project.enums.Role;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.project.repository.ProjectRepository;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectMemberService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    // 팀원 초대
    @Transactional
    public ApiResponse<AddMemberResponse> addMembers(Long loginUserId, Long projectId, AddMemberRequest request) {

        validateLeaderAccess(loginUserId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(PROJECT_NOT_FOUND));

        List<Long> userIds = request.userIds();

        // 이미 프로젝트 멤버인 사람 제외
        List<Long> alreadyMembers = projectMemberRepository
                .findByProject_ProjectIdAndUser_UserIdIn(projectId, userIds)
                .stream()
                .map(pm -> pm.getUser().getUserId())
                .toList();

        // 실제 추가 대상
        List<Long> targetUserIds = userIds.stream()
                .filter(id -> !alreadyMembers.contains(id))
                .toList();

        // 아무도 추가되지 않은 경우
        if (targetUserIds.isEmpty()) {
            return ApiResponse.success(
                    NO_NEW_TEAM_MEMBER,
                    new AddMemberResponse(List.of())
            );
        }

        // 유저 엔티티 조회
        List<User> users = userRepository.findAllById(targetUserIds);

        // 새 ProjectMember 생성
        users.forEach(user -> project.addMember(ProjectMember.createMember(user)));

        projectRepository.save(project);

        return ApiResponse.success(
                ADD_PROJECT_MEMBER_SUCCESS,
                new AddMemberResponse(targetUserIds)
        );
    }

    // 프로젝트 멤버 삭제
    public void deleteMember(Long loginUserId, Long projectId, Long targetUserId) {

        // 1) 로그인 유저가 리더인지 확인
        ProjectMember leader = projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, loginUserId)
                .orElseThrow(() -> new CustomException(FORBIDDEN));

        if (leader.getRole() != Role.LEADER) {
            throw new CustomException(FORBIDDEN); // 리더만 삭제 가능
        }

        // 2) 삭제 대상이 실제 프로젝트 멤버인지 확인
        ProjectMember target = projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, targetUserId)
                .orElseThrow(() -> new CustomException(NOT_PROJECT_MEMBER));

        // 3) 삭제 대상이 리더라면 삭제 불가
        if (target.getRole() == Role.LEADER) {
            throw new CustomException(CANNOT_REMOVE_LEADER);
        }

        // 4) 삭제 수행
        projectMemberRepository.delete(target);
    }

    // 프로젝트 접근 권한 검증
    private void validateLeaderAccess(Long loginUserId, Long projectId) {
        boolean isLeader = projectMemberRepository.existsByProject_ProjectIdAndUser_UserIdAndRole(
                projectId, loginUserId, Role.LEADER
        );

        if (!isLeader) {
            throw new CustomException(FORBIDDEN);  // 403
        }
    }

}
