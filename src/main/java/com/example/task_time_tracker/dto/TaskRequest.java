package com.example.task_time_tracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {
	@NotNull(message = "Duration cannot be null")
	@Min(value = 0, message = "Duration must be a non-negative value")
	private Long duration; // Duration in milliseconds
}
