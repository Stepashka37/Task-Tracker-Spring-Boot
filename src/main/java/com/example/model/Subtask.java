package com.example.model;

import com.example.model.Task;
import com.example.model.TaskStatus;
import com.example.model.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
public class Subtask extends Task {
    protected int epicId;



    public Subtask(int id, String name, String description,  TaskStatus status, TaskType type, LocalDateTime startTime, long duration, LocalDateTime endTime, int epicId) {
        super(id, name, description,  status, type, startTime, duration, endTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }


}
