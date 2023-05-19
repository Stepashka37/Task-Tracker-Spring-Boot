package com.example.dao;

import com.example.exceptions.EpicNotFoundException;
import com.example.exceptions.TaskNotFoundException;
import com.example.model.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class EpicDao extends AbstractTaskDAO<Epic, Epic> {

    private final JdbcTemplate jdbcTemplate;

    public EpicDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Epic> getAll() {
        String sql = "select * from epics";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEpic(rs));
    }

    @Override
    public Epic getById(int id) {
        String sql = "select * from epics where epic_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEpic(rs), id)
                .stream().findAny().orElseThrow(() -> new EpicNotFoundException("Эпик с id " + id + " не найден"));
    }

    @Override
    public Epic create(Epic epic) {
        String sql = "insert into epics (name, description, status, task_type, start_time, end_time, duration) " +
                "values (?,?,?::status,?::task_type,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"epic_id"});
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

        return jdbcTemplate.query("select * from epics where epic_id = ?", (rs, rowNum) -> makeEpic(rs), key)
                .stream().findAny().orElse(null);
    }

    @Override
    public Epic update(Epic epic) {
        String sql = "update epics set " +
                "NAME = ?, DESCRIPTION = ?, STATUS = ?::status, TASK_TYPE = ?::task_type, START_TIME = ?, END_TIME = ?, DURATION = ? " +
                "WHERE epic_id = ?";

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
            throw new EpicNotFoundException("Эпик с  id \"" + epic.getId() + "\" не найден.");
        }

        return jdbcTemplate.query("select * from epics where epic_id = ?", (rs, rowNum) -> makeEpic(rs), epic.getId())
                .stream().findAny().orElse(null);
    }

    @Override
    public void deleteAll() {
        String sql = "delete from epics;" +
                "delete from subtasks;";

        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteById(int id) {
        String sql = "delete from subtasks where epic_id = ?;" +
                "delete from epics where epic_id = ?;";
        jdbcTemplate.update(sql, id, id);
    }

    @Override
    public List<? extends Task> getPrioritized(String period, int count) {
        return null;
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


        /*String sqlForSubtasks = "select * from subtasks as s join epics as e on s.epic_id = e.epic_id " +
                "where s.epic_id = ?";*/

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
    }
}
