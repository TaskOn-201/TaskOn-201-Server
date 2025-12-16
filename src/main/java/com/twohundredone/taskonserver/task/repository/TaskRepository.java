package com.twohundredone.taskonserver.task.repository;

import com.twohundredone.taskonserver.task.entity.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProject_ProjectId(Long projectId);
    void deleteAllByProject_ProjectId(Long projectId);
}
