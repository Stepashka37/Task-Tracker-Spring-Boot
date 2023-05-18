
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


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
        this.endTime = startTime;
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

    public void calculateEpicStatus(){
        Set<TaskStatus> subtasksStats =
                subtasks.stream()
                        .map(x -> x.getStatus())
                        .collect(Collectors.toSet());
        if (this.getSubtasks().isEmpty() || (subtasksStats.size() == 1 && subtasksStats.contains(TaskStatus.NEW))) {
            this.setStatus(TaskStatus.NEW);
        } else if (subtasksStats.size() == 1 && subtasksStats.contains(TaskStatus.DONE)) {
            this.setStatus(TaskStatus.DONE);
        } else {
            this.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}

