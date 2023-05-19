package com.example.dao;

import com.example.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PrioritizedTaskDao {

    private final JdbcTemplate jdbcTemplate;

    public PrioritizedTaskDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<? extends Task> getAll(){
        String sql = "SELECT * FROM (select * from tasks as t " +
                "UNION ALL select * from epics as e " +
                "UNION ALL select subtask_id, name, description,status, task_type, end_time, duration, start_time " +
                "from subtasks as s) ";
        List<? extends Task> allTasks = jdbcTemplate.query(sql, (rs, rowNum) -> makeTask(rs));
        return allTasks;
    }

    public List<? extends Task> getPrioritizedTasks(String period, int count){
        String sql = "SELECT * FROM (select * from tasks as t " +
                "UNION ALL select * from epics as e " +
                "UNION ALL select subtask_id, name, description,status, task_type, end_time, duration, start_time " +
                "from subtasks as s ) " +
                "as t ORDER BY end_time " +
                "LIMIT ?;";
        List<? extends Task> prioritizedTasks = jdbcTemplate.query(sql, (rs, rowNum) -> makeTask(rs), count);
        return prioritizedTasks;
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

  /*  private Subtask makeSubtask(ResultSet rs) throws SQLException {
        Subtask subtaskBuilt = Subtask.builder()
                .id(rs.getInt("subtask_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .status(TaskStatus.convert(rs.getString("status")))
                .type(TaskType.valueOf(rs.getString("task_type")))
                .startTime((rs.getTimestamp("start_time")).toLocalDateTime())
                .endTime((rs.getTimestamp("end_time")).toLocalDateTime())
                .duration(rs.getLong("duration"))
                .epicId(rs.getInt("epic_id"))
                .build();

        return subtaskBuilt;
    }

    private Epic makeEpic(ResultSet rs) throws SQLException {
        Epic epicBuilt = Epic.builder()
                .id(rs.getInt("epic_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .status(TaskStatus.convert(rs.getString("status")))
                .type(TaskType.valueOf(rs.getString("task_type")))
                .startTime((rs.getTimestamp("start_time")).toLocalDateTime())
                .endTime((rs.getTimestamp("end_time")).toLocalDateTime())
                .duration(rs.getLong("duration"))
                .build();


        *//*String sqlForSubtasks = "select * from subtasks as s join epics as e on s.epic_id = e.epic_id " +
                "where s.epic_id = ?";*//*

        String sqlForSubtasks = "select * from subtasks where epic_id = ?";

        List<Subtask> subtasks = jdbcTemplate.query(
                sqlForSubtasks,
                (rss, rowNum) ->
                        new Subtask(
                                rss.getInt("subtask_id"),
                                rss.getString("name"),
                                rss.getString("description"),
                                TaskStatus.convert(rss.getString("status")),
                                TaskType.valueOf(rss.getString("task_type")),
                                LocalDateTime.from((rss.getTimestamp("start_time")).toLocalDateTime()),
                                rss.getLong("duration"),
                                LocalDateTime.from((rss.getTimestamp("end_time")).toLocalDateTime()),
                                rss.getInt("epic_id")
                        ), epicBuilt.getId()
        );
        epicBuilt.setSubtasks(subtasks);

        return epicBuilt;
    }*/



}
