
package com.example.model;

import com.example.model.Subtask;
import com.example.model.Task;
import com.example.model.TaskStatus;
import com.example.model.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SuperBuilder
public class Epic extends Task {
    protected List<Subtask> subtasks = new ArrayList<>();
    protected LocalDateTime endTime;


    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public Epic(int id, String name, String description, TaskStatus status, TaskType type, long duration, LocalDateTime startTime, List<Subtask> subtasks, LocalDateTime endTime) {
        super(id, name, description,  status, type, startTime, duration, endTime);
        this.subtasks = subtasks;
        this.endTime = endTime;
    }

    public void addSubtaskId(Subtask subtask) {
        if (subtasks == null) {
            subtasks = new ArrayList<>();
        }

        subtasks.add(subtask);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void clearSubtasks() {
        subtasks.clear();
        this.startTime = null;
        this.endTime = null;
        this.duration = 0L;
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

