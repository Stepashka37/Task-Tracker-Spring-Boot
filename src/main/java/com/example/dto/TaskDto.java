package com.example.dto;

import com.example.model.TaskStatus;
import com.example.model.TaskType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class TaskDto {

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



}
