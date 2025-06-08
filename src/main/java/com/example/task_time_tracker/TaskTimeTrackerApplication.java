package com.example.task_time_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class TaskTimeTrackerApplication {

    // Define a logger for this class
    private static final Logger logger = LoggerFactory.getLogger(TaskTimeTrackerApplication.class);

    public static void main(String[] args) {
        logger.info("Starting TaskTimeTrackerApplication..."); // Log an informational message
        SpringApplication.run(TaskTimeTrackerApplication.class, args);
        logger.info("TaskTimeTrackerApplication started successfully!"); // Log success after startup
    }
}