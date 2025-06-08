package com.example.task_time_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseTime {
	private String taskId;
	private double averageDuration; // Average duration in milliseconds
}
