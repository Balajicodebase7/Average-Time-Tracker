package com.example.task_time_tracker.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice // Allows handling exceptions across the whole application
public class GlobalExceptionHandler {

	@ExceptionHandler(TaskNotFoundException.class)
	public ResponseEntity<Object> handleTaskNotFoundException(TaskNotFoundException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("error", "Bad Request");
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("error", "Validation Failed");
		// Collect all validation error messages
		String errors = ex.getBindingResult().getAllErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(", "));
		body.put("messages", errors);
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}

	// Generic exception handler as a fallback
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenericException(Exception ex) {
		Map<String, Object> body = new HashMap<>();
		body.put("error", "Internal Server Error");
		body.put("message", "An unexpected error occurred: " + ex.getMessage());
		// It's good practice to log the exception here
		// log.error("Unexpected error occurred", ex);
		return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

