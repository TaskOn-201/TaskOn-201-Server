package com.twohundredone.taskonserver.task.search;

import com.twohundredone.taskonserver.task.search.dto.TaskCommentCountRow;
import com.twohundredone.taskonserver.task.search.dto.TaskParticipantEnrichRow;
import java.util.List;

public interface TaskEnrichQueryRepository {
    List<TaskParticipantEnrichRow> findParticipantsByTaskIds(List<Long> taskIds);
    List<TaskCommentCountRow> findCommentCountsByTaskIds(List<Long> taskIds);
}
