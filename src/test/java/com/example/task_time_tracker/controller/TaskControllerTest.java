
package com.example.task_time_tracker.controller;

import com.example.task_time_tracker.dto.TaskRequest;
import com.example.task_time_tracker.dto.TaskResponseTime;
import com.example.task_time_tracker.exception.TaskNotFoundException;
import com.example.task_time_tracker.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    @Test
    void taskPerformed_validRequest_returnsOk() throws Exception {
        String taskId = "taskA";
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setDuration(150L);

        doNothing().when(taskService).recordTaskPerformed(taskId, 150L);

        mockMvc.perform(post("/tasks/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk());

        verify(taskService).recordTaskPerformed(taskId, 150L);
    }

    @Test
    void taskPerformed_emptyTaskId_returnsBadRequest() throws Exception {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setDuration(100L);

        mockMvc.perform(post("/tasks/ ") // Empty taskId
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).recordTaskPerformed(anyString(), anyLong());
    }
    
    @Test
    void taskPerformed_negativeDuration_returnsBadRequest() throws Exception {
        String taskId = "taskB";
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setDuration(-50L); // Invalid duration

        // This test relies on @Valid and the GlobalExceptionHandler for MethodArgumentNotValidException
        // or direct service throwing IllegalArgumentException if validation is at service layer for this
        // In your DTO, @Min(0) handles this.

        mockMvc.perform(post("/tasks/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.messages", is("Duration must be a non-negative value")));
        
        verify(taskService, never()).recordTaskPerformed(anyString(), anyLong());
    }


    @Test
    void getCurrentAverageTime_taskExists_returnsOkWithData() throws Exception {
        String taskId = "taskC";
        TaskResponseTime responseDto = new TaskResponseTime(taskId, 125.5);

        when(taskService.getCurrentAverageTime(taskId)).thenReturn(responseDto);

        mockMvc.perform(get("/tasks/{taskId}/average", taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.taskId", is(taskId)))
                .andExpect(jsonPath("$.averageDuration", is(125.5)));

        verify(taskService).getCurrentAverageTime(taskId);
    }

    @Test
    void getCurrentAverageTime_taskNotFound_returnsNotFound() throws Exception {
        String taskId = "nonExistentTask";

        when(taskService.getCurrentAverageTime(taskId)).thenThrow(new TaskNotFoundException("Task with ID '" + taskId + "' not found."));

        mockMvc.perform(get("/tasks/{taskId}/average", taskId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task with ID '" + taskId + "' not found.")));

        verify(taskService).getCurrentAverageTime(taskId);
    }
    
    @Test
    void getCurrentAverageTime_emptyTaskId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/tasks/ /average")) // Empty taskId
                .andExpect(status().isBadRequest());

        verify(taskService, never()).getCurrentAverageTime(anyString());
    }
}