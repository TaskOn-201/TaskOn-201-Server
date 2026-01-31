package com.twohundredone.taskonserver.task.repository;

import com.twohundredone.taskonserver.task.dto.TaskParticipantRow;
import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.entity.TaskParticipant;
import com.twohundredone.taskonserver.user.entity.User;
import java.util.List;
import java.util.Optional;

public interface TaskParticipantQueryRepository {

    Optional<TaskParticipant> findNextAssignee(Task task, User excludeUser);

    // Task Board 전용
    List<TaskParticipantRow> findParticipantsByTaskIds(List<Long> taskIds);
    Long findAssigneeUserId(Long taskId);
}
