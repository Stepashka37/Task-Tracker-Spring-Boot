package com.example.dao;

import com.example.exceptions.EpicNotFoundException;
import com.example.exceptions.SubtaskNotFoundException;
import com.example.exceptions.TaskNotFoundException;
import com.example.model.Subtask;
import com.example.model.Task;
import com.example.model.Epic;
import com.example.model.TaskStatus;
import com.example.model.TaskType;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskDao extends AbstractTaskDAO {
    private final JdbcTemplate jdbcTemplate;

    public TaskDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Task> getAll() {
        String sql = "select * from tasks";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeTask(rs));
    }

    @Override
    public Task getById(int id) {
        String sql = "select * from tasks where task_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeTask(rs), id)
                .stream().findAny().orElseThrow(() -> new TaskNotFoundException("Задача с id " + id + " не найдена"));
    }

    @Override
    public Task create(Task task) {
        String sql = "insert into tasks (name, description, status, task_type, start_time, end_time, duration) " +
                "values (?,?,?::status,?::task_type,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"task_id"});
            stmt.setString(1, task.getName());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().toString());
            stmt.setString(4, task.getType().toString());
            stmt.setTimestamp(5, Timestamp.valueOf(task.getStartTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(task.getEndTime()));
            stmt.setLong(7, task.getDuration());
            return stmt;
        }, keyHolder);

        int key = keyHolder.getKey().intValue();
        task.setId(key);

        return jdbcTemplate.query("select * from tasks where task_id = ?", (rs, rowNum) -> makeTask(rs), key)
                .stream().findAny().orElse(null);
    }

    @Override
    public Task update(Task task) {
        String sql = "update tasks set " +
                "NAME = ?, DESCRIPTION = ?, STATUS = ?::status, TASK_TYPE = ?::task_type, START_TIME = ?, END_TIME = ?, DURATION = ? " +
                "WHERE TASK_ID = ?";

        int checkNum = jdbcTemplate.update(sql,
                task.getName(),
                task.getDescription(),
                task.getStatus().toString(),
                task.getType().toString(),
                task.getStartTime(),
                task.getEndTime(),
                task.getDuration(),
                task.getId());

        if (checkNum == 0) {
            throw new TaskNotFoundException("Задача с  id \"" + task.getId() + "\" не найдена.");
        }

        return jdbcTemplate.query("select * from tasks where task_id = ?", (rs, rowNum) -> makeTask(rs), task.getId())
                .stream().findAny().orElse(null);
    }

    @Override
    public void deleteAll() {
        String sql = "delete from tasks";

        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteById(int id) {
        String sql = "delete from tasks where task_id = ?";

        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Task> getPrioritized(String period, int count){
        String sql = "select * from tasks " +
                "order by end_time asc " +
                "limit ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeTask(rs), count);
    }


    private Task makeTask(ResultSet rs) throws SQLException {
        Task taskBuilt = Task.builder()
                .id(rs.getInt("task_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .status(TaskStatus.convert(rs.getString("status")))
                .type(TaskType.valueOf(rs.getString("task_type")))
                .startTime((rs.getTimestamp("start_time")).toLocalDateTime())
                .endTime((rs.getTimestamp("end_time")).toLocalDateTime())
                .duration(rs.getLong("duration"))
                .build();

        return taskBuilt;
    }





}

