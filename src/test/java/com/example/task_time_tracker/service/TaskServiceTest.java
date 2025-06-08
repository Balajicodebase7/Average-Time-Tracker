package com.example.task_time_tracker.service;

import com.example.task_time_tracker.dto.TaskResponseTime;
import com.example.task_time_tracker.exception.TaskNotFoundException;
import com.example.task_time_tracker.model.TaskDetails;
import com.example.task_time_tracker.repository.TaskDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class TaskServiceTest {

	@Mock
	private TaskDetailsRepository taskDetailsRepository;

	@InjectMocks
	private TaskService taskService;

	@Test
	void recordTaskPerformed_newTask() {
		String taskId = "task1";
		long duration = 100L;

		when(taskDetailsRepository.findByTaskId(taskId)).thenReturn(Optional.empty());
		// We can't easily verify the state of 'newTaskDetails' without a captor or
		// custom matcher
		// if we want to check its internal state before save.
		// However, we can verify save is called with any TaskDetails instance.
		when(taskDetailsRepository.save(any(TaskDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));

		taskService.recordTaskPerformed(taskId, duration);

		verify(taskDetailsRepository).findByTaskId(taskId);
		verify(taskDetailsRepository).save(argThat(task -> task.getTaskId().equals(taskId)
				&& task.getDurationTaken() == duration && task.getTaskCount() == 1));
	}

	@Test
	void recordTaskPerformed_existingTask() {
		String taskId = "task1";
		long initialDuration = 100L;
		long initialCount = 2L;
		long newDuration = 50L;

		TaskDetails existingTask = new TaskDetails(taskId);
		existingTask.setDurationTaken(initialDuration); // Simulate existing state
		existingTask.setTaskCount(initialCount); // Simulate existing state
		// Manually call addDuration to set up initial state as if it was already called
		// Or, more simply, just set totalDuration and totalCount.
		// For this test, we assume TaskDetails is constructed and then addDuration is
		// called by the service.
		// So, let's set it up so addDuration works from a base.
		// Corrected approach: set the state on the mock *before* addDuration is called
		// by the service
		existingTask = new TaskDetails(taskId); // Fresh one for clarity
		existingTask.setDurationTaken(100L); // Total duration so far
		existingTask.setTaskCount(2L); // Number of times performed so far

		when(taskDetailsRepository.findByTaskId(taskId)).thenReturn(Optional.of(existingTask));
		when(taskDetailsRepository.save(any(TaskDetails.class))).thenAnswer(invocation -> invocation.getArgument(0));

		taskService.recordTaskPerformed(taskId, newDuration); // Service calls existingTask.addDuration(newDuration)

		verify(taskDetailsRepository).findByTaskId(taskId);
		verify(taskDetailsRepository).save(argThat(
				task -> task.getTaskId().equals(taskId) && task.getDurationTaken() == (initialDuration + newDuration)
						&& task.getTaskCount() == (initialCount + 1)));
	}

	@Test
	void recordTaskPerformed_negativeDuration_throwsIllegalArgumentException() {
		String taskId = "task1";
		long duration = -100L;

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			taskService.recordTaskPerformed(taskId, duration);
		});
		assertEquals("Duration cannot be negative.", exception.getMessage());
		verify(taskDetailsRepository, never()).findByTaskId(anyString());
		verify(taskDetailsRepository, never()).save(any(TaskDetails.class));
	}

	@Test
	void getCurrentAverageTime_taskExists() {
		String taskId = "task1";
		TaskDetails taskDetails = new TaskDetails(taskId);
		taskDetails.addDuration(100L); // Total 100, count 1
		taskDetails.addDuration(200L); // Total 300, count 2

		when(taskDetailsRepository.findByTaskId(taskId)).thenReturn(Optional.of(taskDetails));

		TaskResponseTime response = taskService.getCurrentAverageTime(taskId);

		assertEquals(taskId, response.getTaskId());
		assertEquals(150.0, response.getAverageDuration()); // (100 + 200) / 2
		verify(taskDetailsRepository).findByTaskId(taskId);
	}

	@Test
	void getCurrentAverageTime_taskNotFound_throwsTaskNotFoundException() {
		String taskId = "nonExistentTask";
		when(taskDetailsRepository.findByTaskId(taskId)).thenReturn(Optional.empty());

		TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
			taskService.getCurrentAverageTime(taskId);
		});
		assertEquals("Task with ID '" + taskId + "' not found.", exception.getMessage());
		verify(taskDetailsRepository).findByTaskId(taskId);
	}
}