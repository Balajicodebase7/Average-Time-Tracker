package com.example.task_time_tracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_details")
@Getter
@Setter
@NoArgsConstructor
public class TaskDetails {
	@Id
	@Column(name = "task_id", nullable = false, unique = true)
	private String taskId;

	@Column(name = "duration_taken", nullable = false)
	private long durationTaken = 0L;

	@Column(name = "task_count", nullable = false)
	private long taskCount = 0L;

	@Version
	private Long version;

	public TaskDetails(String taskId) {
		this.taskId = taskId;
	}

	public void addDuration(long duration) {
		if (duration < 0) {
			throw new IllegalArgumentException("Duration cannot be negative.");
		}
		this.durationTaken += duration;
		this.taskCount++;
	}

	public double getAverageDuration() {
		if (this.taskCount == 0) {
			return 0.0;
		}
		return (double) this.durationTaken / this.taskCount;
	}
}
