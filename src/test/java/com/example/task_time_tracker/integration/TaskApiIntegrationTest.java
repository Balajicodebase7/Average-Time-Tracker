package com.example.task_time_tracker.integration;

import com.example.task_time_tracker.dto.TaskRequest;
import com.example.task_time_tracker.dto.TaskResponseTime;
import com.example.task_time_tracker.model.TaskDetails;
import com.example.task_time_tracker.repository.TaskDetailsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional; // Optional: for cleaning up data if needed

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest // Loads the full application context
@AutoConfigureMockMvc // Configures MockMvc
// @ActiveProfiles("test") // Optional: if you have a specific test application.properties
class TaskTimeTrackerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskDetailsRepository taskDetailsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clean up the database before each test to ensure independence
        taskDetailsRepository.deleteAll();
    }

    @Test
    void testRecordAndRetrieveAverage_NewTask() throws Exception {
        String taskId = "integrationTestTask1";
        TaskRequest request1 = new TaskRequest();
        request1.setDuration(100L);

        // Record first duration
        mockMvc.perform(post("/tasks/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Verify average
        mockMvc.perform(get("/tasks/{taskId}/average", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId", is(taskId)))
                .andExpect(jsonPath("$.averageDuration", is(100.0)));

        // Record second duration
        TaskRequest request2 = new TaskRequest();
        request2.setDuration(200L);
        mockMvc.perform(post("/tasks/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        // Verify updated average
        mockMvc.perform(get("/tasks/{taskId}/average", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId", is(taskId)))
                .andExpect(jsonPath("$.averageDuration", is(150.0))); // (100 + 200) / 2
    }

    @Test
    void testGetAverage_TaskNotFound() throws Exception {
        String taskId = "nonExistentTask";
        mockMvc.perform(get("/tasks/{taskId}/average", taskId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task with ID '" + taskId + "' not found.")));
    }

    @Test
    void testRecordTask_InvalidDuration() throws Exception {
        String taskId = "taskWithInvalidDuration";
        TaskRequest request = new TaskRequest();
        request.setDuration(-50L); // Invalid: negative duration

        mockMvc.perform(post("/tasks/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.messages", is("Duration must be a non-negative value")));
    }
    
    @Test
    void testRecordTask_NullDuration() throws Exception {
        String taskId = "taskWithNullDuration";
        // Create a JSON string with null duration, as TaskRequest object would set default 0L
        String jsonRequest = "{\"duration\":null}";

        mockMvc.perform(post("/tasks/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.messages", is("Duration cannot be null")));
    }
}