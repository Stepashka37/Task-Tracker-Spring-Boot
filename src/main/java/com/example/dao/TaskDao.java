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

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskDao {
    private final JdbcTemplate jdbcTemplate;

    public TaskDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /** Task functionality */
    public List<Task> getAllTasks() {
        String sql = "select * from tasks where task_type = 'TASK'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeTask(rs));
    }

    public Task getTaskById(int id) {
        String sql = "select * from tasks where task_id = ? AND task_type = 'TASK'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeTask(rs), id)
                .stream().findAny().orElseThrow(() -> new TaskNotFoundException("Задача с id " + id + " не найдена"));
    }

    public Task createNewTask(Task task) {
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

    public Task updateTask(Task task) {
        String sql = "update tasks set " +
                "NAME = ?, DESCRIPTION = ?, STATUS = ?::status, TASK_TYPE = ?::task_type, START_TIME = ?, END_TIME = ?, DURATION = ? " +
                "WHERE (TASK_ID = ? AND TASK_TYPE = 'TASK')";

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
        String sql = "delete from tasks WHERE task_type='TASK'";

        jdbcTemplate.update(sql);
    }

    public void deleteTaskById(int id) {
        String sql = "delete from tasks where task_id = ? and task_type = 'TASK'";

        jdbcTemplate.update(sql, id);
    }


    /** Epic functionality */

    public List<Epic> getAllEpics() {
        String sql = "select * from tasks where task_type = 'EPIC'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEpic(rs));
    }

    public Epic getEpicById(int id) {
        String sql = "select * from tasks where task_id = ? AND task_type = 'EPIC'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEpic(rs), id)
                .stream().findAny().orElseThrow(() -> new EpicNotFoundException("Эпик с id " + id + " не найден"));
    }

    public List<Subtask> getEpicSubtasks(int id) {
        String sql = "select * from tasks where task_id in (select es.subtask_id from epic_subtasks as es join tasks as t on es.subtask_id = t.task_id " +
                "where es.epic_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeSubtask(rs), id);
    }

    public Epic createNewEpic(Epic epic) {
        String sql = "insert into tasks (name, description, status, task_type, start_time, end_time, duration) " +
                "values (?,?,?::status,?::task_type,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"task_id"});
            stmt.setString(1, epic.getName());
            stmt.setString(2, epic.getDescription());
            stmt.setString(3, epic.getStatus().toString());
            stmt.setString(4, epic.getType().toString());
            stmt.setTimestamp(5, Timestamp.valueOf(epic.getStartTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(epic.getEndTime()));
            stmt.setLong(7, epic.getDuration());
            return stmt;
        }, keyHolder);

        int key = keyHolder.getKey().intValue();
        epic.setId(key);

        return jdbcTemplate.query("select * from tasks where task_id = ?", (rs, rowNum) -> makeEpic(rs), key)
                .stream().findAny().orElse(null);
    }

    public Epic updateEpic(Epic epic) {
        String sql = "update tasks set " +
                "NAME = ?, DESCRIPTION = ?, STATUS = ?::status, TASK_TYPE = ?::task_type, START_TIME = ?, END_TIME = ?, DURATION = ? " +
                "WHERE (TASK_ID = ? AND TYPE = 'EPIC')";

        int checkNum = jdbcTemplate.update(sql,
                epic.getName(),
                epic.getDescription(),
                epic.getStatus().toString(),
                epic.getType().toString(),
                epic.getStartTime(),
                epic.getEndTime(),
                epic.getDuration(),
                epic.getId());

        if (checkNum == 0) {
            throw new TaskNotFoundException("Эпик с  id \"" + epic.getId() + "\" не найден.");
        }

        return jdbcTemplate.query("select * from tasks where task_id = ?", (rs, rowNum) -> makeEpic(rs), epic.getId())
                .stream().findAny().orElse(null);
    }

    public void deleteAllEpics() {
        String sql = "delete from tasks WHERE task_type='EPIC';" +
                "delete from tasks WHERE task_type = 'SUBTASK';" +
                "delete from epic_subtasks";

        jdbcTemplate.update(sql);
    }

    public void deleteEpicById(int id) {
        String sql = "delete from tasks where task_id = ? AND task_type = 'EPIC';" +
        "delete from tasks where task_id in (select es.subtask_id from epic_subtasks as es join tasks as t on es.subtask_id = t.task_id " +
        "where es.epic_id = ?);" +
        "delete from epic_subtasks where epic_id = ?;";
        jdbcTemplate.update(sql, id, id, id);
    }

    /** Subtask functionality */

    public List<Subtask> getAllSubtasks() {
        String sql = "select * from tasks where task_type = 'SUBTASK'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeSubtask(rs));
    }

    public Subtask getSubtaskById(int id) {
        String sql = "select * from tasks where task_id = ? AND task_type = 'SUBTASK'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeSubtask(rs), id)
                .stream().findAny().orElseThrow(() -> new SubtaskNotFoundException("Подзадача с id " + id + " не найдена"));
    }

    public Subtask createNewSubtask(Subtask subtask) {
        String sql = "insert into tasks (name, description, status, task_type, start_time, end_time, duration) " +
                "values (?,?,?::status,?::task_type,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"task_id"});
            stmt.setString(1, subtask.getName());
            stmt.setString(2, subtask.getDescription());
            stmt.setString(3, subtask.getStatus().toString());
            stmt.setString(4, subtask.getType().toString());
            stmt.setTimestamp(5, Timestamp.valueOf(subtask.getStartTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(subtask.getEndTime()));
            stmt.setLong(7, subtask.getDuration());
            return stmt;
        }, keyHolder);

        int key = keyHolder.getKey().intValue();
        subtask.setId(key);

        String sqlForEpicSubtasks = "insert into epic_subtasks(epic_id, subtask_id) " +
                " values (?, ?)";

        jdbcTemplate.update(sqlForEpicSubtasks, subtask.getEpicId(), subtask.getId());

        return jdbcTemplate.query("select * from tasks where task_id = ?", (rs, rowNum) -> makeSubtask(rs), key)
                .stream().findAny().orElse(null);
    }

    public Subtask updateSubtask(Subtask subtask) {

        String sql = "update tasks set " +
                "NAME = ?, DESCRIPTION = ?, STATUS = ?::status, TASK_TYPE = ?::task_type, START_TIME = ?, END_TIME = ?, DURATION = ? " +
                "WHERE (TASK_ID = ? AND type = 'SUBTASK')";

        int checkNum = jdbcTemplate.update(sql,
                subtask.getName(),
                subtask.getDescription(),
                subtask.getStatus().toString(),
                subtask.getType().toString(),
                subtask.getStartTime(),
                subtask.getEndTime(),
                subtask.getDuration(),
                subtask.getId());

        if (checkNum == 0) {
            throw new SubtaskNotFoundException("Подзадача с  id \"" + subtask.getId() + "\" не найдена.");
        }

        return jdbcTemplate.query("select * from tasks where task_id = ?", (rs, rowNum) -> makeSubtask(rs), subtask.getId())
                .stream().findAny().orElse(null);
    }

    public void deleteAllSubtasks() {
        String sql = "delete from tasks WHERE task_type = 'SUBTASK';" +
                "delete from epic_subtasks";

        jdbcTemplate.update(sql);
    }

    public void deleteSubtaskById(int id) {
        String sql = "delete from tasks where task_id = ? AND task_type = 'SUBTASK';" +
                "delete from epic_subtasks where subtask_id =?";
        jdbcTemplate.update(sql, id, id);
    }



    private Task makeTask(ResultSet rs) throws SQLException {
        Task taskBuilt = Task.builder()
                .id(rs.getInt("task_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .status(TaskStatus.valueOf(rs.getString("status")))
                .type(TaskType.valueOf(rs.getString("task_type")))
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
                .type(TaskType.valueOf(rs.getString("task_type")))
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
                                TaskType.valueOf(rss.getString("task_type")),
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
                .type(TaskType.valueOf(rs.getString("task_type")))
                .startTime((rs.getTimestamp("start_time")).toLocalDateTime())
                .endTime((rs.getTimestamp("end_time")).toLocalDateTime())
                .duration(rs.getLong("duration"))
                .build();

        //int epicId = jdbcTemplate.queryForObject("select epic_id from epic_subtasks where subtask_id = ?", new Object[]{subtaskBuilt.getId()}, Integer.class);

        //subtaskBuilt.setEpicId(epicId);
        return subtaskBuilt;
    }

    /*private void addSubtasks(Epic epic) {
        String sqlQueryForSubtasks = "merge into tasks (name, description, status, type, start_time, end_time, duration) " +
                "values (?,?,?::status,?::type,?,?,?)";
        List<Subtask> genres = new ArrayList<>(epic.getSubtasks());
        jdbcTemplate.batchUpdate(sqlQueryForSubtasks, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setString(1, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }*/



}

