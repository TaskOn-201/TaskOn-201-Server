package com.twohundredone.taskonserver.task.repository;

import com.twohundredone.taskonserver.task.entity.TaskParticipant;
import com.twohundredone.taskonserver.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskParticipantRepository extends JpaRepository<TaskParticipant, Long> {
    List<TaskParticipant> findAllByTask_TaskId(Long taskId);
    Optional<TaskParticipant> findByTask_TaskIdAndUser_UserId(Long taskId, Long userId);
    List<TaskParticipant> findAllByTask_TaskIdAndUser_UserIdIn(Long taskId, List<Long> userIds);
    List<TaskParticipant> findAllByUser(User user);
    void deleteAllByTask_TaskId(Long taskId);
}
