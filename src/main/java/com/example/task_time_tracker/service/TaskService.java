package com.example.task_time_tracker.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Important for data consistency

import com.example.task_time_tracker.dto.TaskResponseTime;
import com.example.task_time_tracker.exception.TaskNotFoundException;
import com.example.task_time_tracker.model.TaskDetails;
import com.example.task_time_tracker.repository.TaskDetailsRepository;

import java.util.Optional;

@Service
public class TaskService {

	private final TaskDetailsRepository taskDetailsRepository;

	@Autowired
	public TaskService(TaskDetailsRepository taskDetailsRepository) {
		this.taskDetailsRepository = taskDetailsRepository;
	}

	/**
	 * Records a performed task's duration and updates its statistics. If the task
	 * is new, it's created. This operation is transactional to ensure atomicity.
	 *
	 * @param taskId     The unique identifier of the task.
	 * @param durationMs The duration of the task performed, in milliseconds.
	 * @throws IllegalArgumentException if durationMs is negative.
	 */
	@Transactional // Ensures that the read and write operations are atomic
	public void recordTaskPerformed(String taskId, long durationMs) {
		if (durationMs < 0) {
			throw new IllegalArgumentException("Duration cannot be negative.");
		}

		// Find existing task or create a new one
		TaskDetails taskDetails = taskDetailsRepository.findByTaskId(taskId).orElseGet(() -> new TaskDetails(taskId));

		taskDetails.addDuration(durationMs);
		taskDetailsRepository.save(taskDetails); // Saves new or updates existing
	}

	/**
	 * Retrieves the current average time for a given task.
	 *
	 * @param taskId The unique identifier of the task.
	 * @return A TaskAverageResponse containing the task ID and its average
	 *         duration.
	 * @throws TaskNotFoundException if the task with the given ID does not exist.
	 */
	@Transactional(readOnly = true) // Read-only transaction as we are only fetching data
	public TaskResponseTime getCurrentAverageTime(String taskId) {
		TaskDetails taskDetails = taskDetailsRepository.findByTaskId(taskId)
				.orElseThrow(() -> new TaskNotFoundException("Task with ID '" + taskId + "' not found."));

		return new TaskResponseTime(taskDetails.getTaskId(), taskDetails.getAverageDuration());
	}
}

