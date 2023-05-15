package com.example.model;

import lombok.Builder;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;


@SuperBuilder
public class Task {

    protected int id;

    @NotBlank(message = "Имя не может быть пустым")
    protected String name;

    @Size(min = 0, max = 500)
    protected String description;

    @NotNull
    protected TaskStatus status;

    @NotNull
    protected TaskType type;

    @NotNull
    protected LocalDateTime startTime;

    @Positive
    protected long duration;

    @NotNull
    protected LocalDateTime endTime;

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    /*public Task(int id, String name, String description, TaskStatus status, long duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.type = TaskType.TASK;
    } */

    public Task(int id, String name, String description,  TaskStatus status, TaskType type, LocalDateTime startTime, long duration, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = type;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = endTime;
    }

    /* public Task(String name, String description, TaskStatus status, long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }


    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }*/

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && type == task.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, type);
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}