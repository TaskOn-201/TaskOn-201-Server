package com.twohundredone.taskonserver.chat.service;

import static com.twohundredone.taskonserver.chat.enums.ChatType.PROJECT_GROUP;
import static com.twohundredone.taskonserver.chat.enums.ChatType.TASK_GROUP;

import com.twohundredone.taskonserver.chat.entity.ChatRoom;
import com.twohundredone.taskonserver.chat.entity.ChatUser;
import com.twohundredone.taskonserver.chat.enums.ChatType;
import com.twohundredone.taskonserver.chat.repository.ChatMessageRepository;
import com.twohundredone.taskonserver.chat.repository.ChatRoomRepository;
import com.twohundredone.taskonserver.chat.repository.ChatUserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatDomainServiceImpl implements ChatDomainService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatMessageRepository chatMessageRepository;

    /* ================= Project ================= */

    @Override
    public void onProjectCreated(Long projectId, Long leaderUserId) {
        ChatRoom room = chatRoomRepository.save(
                ChatRoom.builder()
                        .chatType(PROJECT_GROUP)
                        .projectId(projectId)
                        .chatRoomName("PROJECT-" + projectId)
                        .build()
        );

        chatUserRepository.save(
                ChatUser.builder()
                        .chatRoom(room)
                        .userId(leaderUserId)
                        .lastReadAt(LocalDateTime.now())
                        .build()
        );
    }

    @Override
    public void onProjectMembersAdded(Long projectId, List<Long> userIds) {
        ChatRoom room = findProjectRoom(projectId);

        userIds.forEach(userId -> {
            if (!chatUserRepository.existsByChatRoom_ChatIdAndUserId(room.getChatId(), userId)) {
                chatUserRepository.save(
                        ChatUser.builder()
                                .chatRoom(room)
                                .userId(userId)
                                .lastReadAt(LocalDateTime.now())
                                .build()
                );
            }
        });
    }

    @Override
    public void onProjectMemberRemoved(Long projectId, Long userId) {
        ChatRoom room = findProjectRoom(projectId);
        chatUserRepository.deleteByChatRoom_ChatIdAndUserId(room.getChatId(), userId);
    }

    @Override
    public void onProjectDeleted(Long projectId) {
        ChatRoom room = findProjectRoom(projectId);
        deleteRoomCascade(room);
    }

    /* ================= Task ================= */

    @Override
    public void onTaskCreated(Long taskId, List<Long> participantUserIds) {
        if (chatRoomRepository.existsByChatTypeAndTaskId(TASK_GROUP, taskId)) {
            ChatRoom room = chatRoomRepository
                    .findByChatTypeAndTaskId(TASK_GROUP, taskId)
                    .orElseThrow();

            syncParticipants(room.getChatId(), participantUserIds);
            return;
        }

        ChatRoom room = chatRoomRepository.save(
                ChatRoom.builder()
                        .chatType(TASK_GROUP)
                        .taskId(taskId)
                        .chatRoomName("TASK-" + taskId)
                        .build()
        );

        participantUserIds.forEach(userId ->
                chatUserRepository.save(
                        ChatUser.builder()
                                .chatRoom(room)
                                .userId(userId)
                                .lastReadAt(LocalDateTime.now())
                                .build()
                )
        );
    }

    @Override
    public void onTaskParticipantsChanged(Long taskId, List<Long> participantUserIds) {
        ChatRoom room = findTaskRoom(taskId);
        syncParticipants(room.getChatId(), participantUserIds);
    }

    @Override
    public void onTaskDeleted(Long taskId) {
        ChatRoom room = findTaskRoom(taskId);
        deleteRoomCascade(room);
    }

    /* ================= 공통 ================= */

    private void syncParticipants(Long chatRoomId, List<Long> newUserIds) {
        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow();

        List<ChatUser> existing =
                chatUserRepository.findAllByChatRoom_ChatId(chatRoomId);

        Set<Long> existingIds = existing.stream()
                .map(ChatUser::getUserId)
                .collect(Collectors.toSet());

        Set<Long> newIds = new HashSet<>(newUserIds);

        // 추가
        newIds.stream()
                .filter(id -> !existingIds.contains(id))
                .forEach(id -> chatUserRepository.save(
                        ChatUser.builder()
                                .chatRoom(room)
                                .userId(id)
                                .lastReadAt(LocalDateTime.now())
                                .build()
                ));

        // 삭제
        List<ChatUser> toDelete = existing.stream()
                .filter(cu -> !newIds.contains(cu.getUserId()))
                .toList();

        chatUserRepository.deleteAllInBatch(toDelete);
    }

    private void deleteRoomCascade(ChatRoom room) {
        chatMessageRepository.deleteAllByChatRoom_ChatId(room.getChatId());
        chatUserRepository.deleteAllByChatRoom_ChatId(room.getChatId());
        chatRoomRepository.delete(room);
    }

    private ChatRoom findProjectRoom(Long projectId) {
        return chatRoomRepository
                .findByChatTypeAndProjectId(PROJECT_GROUP, projectId)
                .orElseThrow();
    }

    private ChatRoom findTaskRoom(Long taskId) {
        return chatRoomRepository
                .findByChatTypeAndTaskId(TASK_GROUP, taskId)
                .orElseThrow();
    }
}
