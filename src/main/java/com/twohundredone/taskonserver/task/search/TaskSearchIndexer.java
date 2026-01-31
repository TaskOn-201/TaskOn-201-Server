package com.twohundredone.taskonserver.task.search;

import com.twohundredone.taskonserver.task.entity.Task;
import com.twohundredone.taskonserver.task.repository.TaskParticipantQueryRepository;
import com.twohundredone.taskonserver.task.repository.TaskRepository;
import com.twohundredone.taskonserver.task.search.event.TaskChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskSearchIndexer {
    private final TaskRepository taskRepository;
    private final TaskSearchRepository taskSearchRepository;
    private final TaskParticipantQueryRepository taskParticipantQueryRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TaskChangedEvent event) {
        Long taskId = event.getTaskId();

        if (event.getChangeType() == TaskChangedEvent.ChangeType.DELETED) {
            taskSearchRepository.deleteById(taskId);
            return;
        }

        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) return;

        Long assigneeId = taskParticipantQueryRepository.findAssigneeUserId(taskId);
        TaskSearchDocument doc = TaskSearchDocument.from(task, assigneeId);

        taskSearchRepository.save(doc);
    }
}
