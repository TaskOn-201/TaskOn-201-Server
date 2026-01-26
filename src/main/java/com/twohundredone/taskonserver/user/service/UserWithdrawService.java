package com.twohundredone.taskonserver.user.service;

import static com.twohundredone.taskonserver.global.enums.ResponseStatusError.USER_NOT_FOUND;
import static com.twohundredone.taskonserver.project.enums.Role.LEADER;

import com.twohundredone.taskonserver.auth.service.RefreshTokenService;
import com.twohundredone.taskonserver.auth.util.CookieUtil;
import com.twohundredone.taskonserver.chat.repository.ChatUserRepository;
import com.twohundredone.taskonserver.comment.repository.CommentRepository;
import com.twohundredone.taskonserver.global.exception.CustomException;
import com.twohundredone.taskonserver.project.entity.ProjectMember;
import com.twohundredone.taskonserver.project.enums.Role;
import com.twohundredone.taskonserver.project.repository.ProjectMemberQueryRepository;
import com.twohundredone.taskonserver.project.repository.ProjectMemberRepository;
import com.twohundredone.taskonserver.task.entity.TaskParticipant;
import com.twohundredone.taskonserver.task.enums.TaskRole;
import com.twohundredone.taskonserver.task.repository.TaskParticipantQueryRepository;
import com.twohundredone.taskonserver.task.repository.TaskParticipantRepository;
import com.twohundredone.taskonserver.task.service.TaskBoardCacheEvictor;
import com.twohundredone.taskonserver.user.entity.User;
import com.twohundredone.taskonserver.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserWithdrawService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final OnlineStatusService onlineStatusService;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskParticipantRepository taskParticipantRepository;
    private final ChatUserRepository chatUserRepository;
    private final CommentRepository commentRepository;
    private final ProjectMemberQueryRepository projectMemberQueryRepository;
    private final TaskParticipantQueryRepository taskParticipantQueryRepository;
    private final TaskBoardCacheEvictor taskBoardCacheEvictor;

    @Transactional
    public void withdraw(Long userId, HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 캐시 무효화를 위한 projectId 수집
        List<Long> projectIds =
                projectMemberRepository.findAllByUser_UserId(user.getUserId())
                        .stream()
                        .map(pm -> pm.getProject().getProjectId())
                        .toList();

        handleProjectMembers(user);
        handleTaskParticipants(user);

        chatUserRepository.deleteAllByUserId(userId);
        commentRepository.clearUserReference(userId);

        refreshTokenService.delete(userId);
        onlineStatusService.setOffline(userId);

        userRepository.delete(user); // Hard Delete
        CookieUtil.deleteRefreshTokenCookie(request, response);

        // TaskBoard 캐시 무효화
        projectIds.forEach(taskBoardCacheEvictor::evictProjectBoard);
    }

    private void handleProjectMembers(User user) {
        List<ProjectMember> members =
                projectMemberRepository.findAllByUser_UserId(user.getUserId());

        for (ProjectMember member : members) {
            if (member.getRole() == LEADER) {
                projectMemberQueryRepository
                        .findNextLeader(member.getProject(), user)
                        .ifPresent(nextLeader ->
                                nextLeader.changeRole(LEADER));
            }
            projectMemberRepository.delete(member);
        }
    }

    private void handleTaskParticipants(User user) {
        List<TaskParticipant> participants =
                taskParticipantRepository.findAllByUser(user);

        for (TaskParticipant participant : participants) {
            if (participant.isAssignee()) {

                boolean delegated = taskParticipantQueryRepository
                        .findNextAssignee(participant.getTask(), user)
                        .map(next -> {
                            next.updateRole(TaskRole.ASSIGNEE);
                            return true;
                        })
                        .orElse(false);

                // 위임 실패한 경우에만 ARCHIVED
                if (!delegated) {
                    participant.getTask().archive();
                }
            }

            taskParticipantRepository.delete(participant);
        }
    }
}
