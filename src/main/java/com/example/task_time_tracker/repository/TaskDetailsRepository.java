package com.example.task_time_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.task_time_tracker.model.TaskDetails;

import java.util.Optional;

@Repository
public interface TaskDetailsRepository extends JpaRepository<TaskDetails, String> {
	// JpaRepository provides CRUD operations like save(), findById(), findAll(),
	// deleteById() etc.
	// We can define custom query methods if needed, but for this exercise,
	// findById will be sufficient.
	Optional<TaskDetails> findByTaskId(String taskId);
}
