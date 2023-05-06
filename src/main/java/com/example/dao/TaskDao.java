package com.example.dao;

import com.example.exceptions.EpicNotFoundException;
import com.example.exceptions.SubtaskNotFoundException;
import com.example.exceptions.TaskNotFoundException;
import com.example.model.Subtask;
import com.example.model.Task;
import com.example.model.Epic;
import com.example.model.TaskStatus;
import com.example.model.TaskType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@Component
public class TaskDao {
    private final JdbcTemplate jdbcTemplate;

    public TaskDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /** Task functionality */
    public List<Task> getAllTasks() {
        String sql = "select * from tasks where type = 'TASK'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeTask(rs));
    }

    public Task getTaskById(int id) {
        String sql = "select * from tasks where task_id = ? AND type = 'TASK'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeTask(rs), id)
                .stream().findAny().orElseThrow(() -> new TaskNotFoundException("Задача с id " + id + " не найдена"));
    }

    public Task createNewTask(Task task) {
        String sql = "insert into tasks (name, description, status, type, start_time, end_time, duration) " +
                "values (?,?,?::status,?::type,?,?,?)";

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

    public Task updateTask(Task task) {
        String sql = "update tasks set " +
                "NAME = ?, DESCRIPTION = ?, STATUS = ?::status, TYPE = ?::type, START_TIME = ?, END_TIME = ?, DURATION = ? " +
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

    public void deleteAllTasks() {
        String sql = "delete from tasks WHERE type='TASK'";

        jdbcTemplate.update(sql);
    }

    public void deleteTaskById(int id) {
        String sql = "delete from tasks where task_id = ?";

        jdbcTemplate.update(sql, id);
    }


    /** Epic functionality */

    public List<Epic> getAllEpics() {
        String sql = "select * from tasks where type = 'EPIC'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEpic(rs));
    }

    public Epic getEpicById(int id) {
        String sql = "select * from tasks where task_id = ? AND type = 'EPIC'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEpic(rs), id)
                .stream().findAny().orElseThrow(() -> new EpicNotFoundException("Эпик с id " + id + " не найден"));
    }

    /** Subtask functionality */

    public List<Subtask> getAllSubtasks() {
        String sql = "select * from tasks where type = 'SUBTASK'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeSubtask(rs));
    }

    public Subtask getSubtaskById(int id) {
        String sql = "select * from tasks where task_id = ? AND type = 'SUBTASK'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeSubtask(rs), id)
                .stream().findAny().orElseThrow(() -> new SubtaskNotFoundException("Подзадача с id " + id + " не найдена"));
    }



    private Task makeTask(ResultSet rs) throws SQLException {
        Task taskBuilt = Task.builder()
                .id(rs.getInt("task_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .status(TaskStatus.valueOf(rs.getString("status")))
                .type(TaskType.valueOf(rs.getString("type")))
                .startTime((rs.getTimestamp("start_time")).toLocalDateTime())
                .endTime((rs.getTimestamp("end_time")).toLocalDateTime())
                .duration(rs.getLong("duration"))
                .build();

        return taskBuilt;
    }

    private Epic makeEpic(ResultSet rs) throws SQLException {
        Epic epicBuilt = Epic.builder()
                .id(rs.getInt("task_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .status(TaskStatus.valueOf(rs.getString("status")))
                .type(TaskType.valueOf(rs.getString("type")))
                .startTime((rs.getTimestamp("start_time")).toLocalDateTime())
                .endTime((rs.getTimestamp("end_time")).toLocalDateTime())
                .duration(rs.getLong("duration"))
                .build();


        String sqlForSubtasks = "select * from epic_subtasks as es join tasks as t on es.subtask_id = t.task_id " +
                "where es.epic_id = ?";

        List<Subtask> subtasks = jdbcTemplate.query(
                sqlForSubtasks,
                (rss, rowNum) ->
                        new Subtask(
                                rss.getInt("subtask_id"),
                                rss.getString("name"),
                                rss.getString("description"),
                                TaskStatus.valueOf(rss.getString("status")),
                                TaskType.valueOf(rss.getString("type")),
                                LocalDateTime.from((rss.getTimestamp("start_time")).toLocalDateTime()),
                                rss.getLong("duration"),
                                LocalDateTime.from((rss.getTimestamp("end_time")).toLocalDateTime()),
                                rss.getInt("epic_id")
                        ), epicBuilt.getId()
        );
        epicBuilt.setSubtasks(subtasks);

        return epicBuilt;
    }

    private Subtask makeSubtask(ResultSet rs) throws SQLException {
        Subtask subtaskBuilt = Subtask.builder()
                .id(rs.getInt("task_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .status(TaskStatus.valueOf(rs.getString("status")))
                .type(TaskType.valueOf(rs.getString("type")))
                .startTime((rs.getTimestamp("start_time")).toLocalDateTime())
                .endTime((rs.getTimestamp("end_time")).toLocalDateTime())
                .duration(rs.getLong("duration"))
                .build();

        int epicId = jdbcTemplate.queryForObject("select epic_id from epic_subtasks where subtask_id = ?", new Object[]{subtaskBuilt.getId()}, Integer.class);

        subtaskBuilt.setEpicId(epicId);
        return subtaskBuilt;
    }



}

