package com.example.task_time_tracker.exception;

public class TaskNotFoundException extends RuntimeException {
	public TaskNotFoundException(String message) {
		super(message);
	}
}
