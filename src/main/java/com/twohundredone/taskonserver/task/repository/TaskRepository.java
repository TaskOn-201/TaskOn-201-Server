package com.twohundredone.taskonserver.task.repository;

import com.twohundredone.taskonserver.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
