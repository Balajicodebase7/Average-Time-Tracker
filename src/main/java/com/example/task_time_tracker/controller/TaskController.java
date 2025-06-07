package com.example.task_time_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.task_time_tracker.dto.TaskRequest;
import com.example.task_time_tracker.dto.TaskResponseTime;
import com.example.task_time_tracker.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks") // Base path for all task-related endpoints
public class TaskController {

	private final TaskService taskService;

	@Autowired
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * Endpoint to record that a task has been performed. Accepts the task
	 * identifier as a path variable and duration in the request body.
	 *
	 * @param taskId  The unique identifier of the task.
	 * @param request The request body containing the duration.
	 * @return ResponseEntity with HTTP status OK (200) if successful.
	 */
	@PostMapping("/{taskId}")
	public ResponseEntity<Void> taskPerformed(@PathVariable String taskId, @Valid @RequestBody TaskRequest request) {
		// Basic validation for taskId can be added here if needed (e.g., not empty)
		if (taskId == null || taskId.trim().isEmpty()) {
			return ResponseEntity.badRequest().build(); // Or throw a custom exception
		}
		taskService.recordTaskPerformed(taskId, request.getDuration());
		return ResponseEntity.ok().build();
	}

	/**
	 * Endpoint to get the current average time for a specific task. Accepts the
	 * task identifier as a path variable.
	 *
	 * @param taskId The unique identifier of the task.
	 * @return ResponseEntity with the task ID and its average duration, and HTTP
	 *         status OK (200). Returns HTTP status NOT_FOUND (404) if the task
	 *         doesn't exist.
	 */
	@GetMapping("/{taskId}/average")
	public ResponseEntity<TaskResponseTime> getCurrentAverageTime(@PathVariable String taskId) {
		if (taskId == null || taskId.trim().isEmpty()) {
			return ResponseEntity.badRequest().build(); // Or throw a custom exception
		}
		TaskResponseTime response = taskService.getCurrentAverageTime(taskId);
		return ResponseEntity.ok(response);
	}
}
