package com.example.dao;

import com.example.exceptions.SubtaskNotFoundException;
import com.example.model.*;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class SubtaskDao extends AbstractTaskDAO<Subtask, Subtask> {

    private final JdbcTemplate jdbcTemplate;

    public SubtaskDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Subtask> getAll() {
        String sql = "select * from subtasks";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeSubtask(rs));
    }

    @Override
    public Subtask getById(int id) {
        String sql = "select * from subtasks where subtask_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeSubtask(rs), id)
                .stream().findAny().orElseThrow(() -> new SubtaskNotFoundException("Подзадача с id " + id + " не найдена"));
    }

    public List<Subtask> getEpicSubtasks(int id) {
        /*String sql = "select * from subtasks where subtask_id in (select es.subtask_id from epic_subtasks as es join tasks as t on es.subtask_id = t.task_id " +
                "where es.epic_id = ?)";*/
        String sql = "select * from subtasks where epic_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeSubtask(rs), id);
    }

    @Override
    public Subtask create(Subtask subtask) {
        String sql = "insert into subtasks (name, description, status, task_type, start_time, end_time, duration, epic_id) " +
                "values (?,?,?::status,?::task_type,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"subtask_id"});
            stmt.setString(1, subtask.getName());
            stmt.setString(2, subtask.getDescription());
            stmt.setString(3, subtask.getStatus().toString());
            stmt.setString(4, subtask.getType().toString());
            stmt.setTimestamp(5, Timestamp.valueOf(subtask.getStartTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(subtask.getEndTime()));
            stmt.setLong(7, subtask.getDuration());
            stmt.setInt(8, subtask.getEpicId());
            return stmt;
        }, keyHolder);

        int key = keyHolder.getKey().intValue();
        subtask.setId(key);

        Subtask subtaskCreated = jdbcTemplate.query("select * from subtasks where subtask_id = ?", (rs, rowNum) -> makeSubtask(rs), key)
                .stream().findAny().orElse(null);

        calculateEpicStatus(subtaskCreated.getEpicId());
        updateTime(subtaskCreated.getEpicId());

        return subtaskCreated;
    }

    @Override
    public Subtask update(Subtask subtask) {
        String sql = "update subtasks set " +
                "NAME = ?, DESCRIPTION = ?, STATUS = ?::status, TASK_TYPE = ?::task_type, START_TIME = ?, END_TIME = ?, DURATION = ? " +
                "WHERE subtask_id = ?";

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

        Subtask subtaskUpdated = jdbcTemplate.query("select * from subtasks where subtask_id = ?", (rs, rowNum) -> makeSubtask(rs), subtask.getId())
                .stream().findAny().orElse(null);

        calculateEpicStatus(subtaskUpdated.getEpicId());
        updateTime(subtaskUpdated.getEpicId());

        return subtaskUpdated;
    }

    @Override
    public void deleteAll() {
        String sql = "delete from subtasks;" +
                "delete from epics where epic_id in (select epic_id from subtasks);";

        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteById(int id) {
        Subtask subtaskToDelete = jdbcTemplate.query("select * from subtasks where subtask_id = ?", (rs, rowNum) -> makeSubtask(rs), id)
                .stream().findAny().get();

        if (jdbcTemplate.query("select * from subtasks where epic_id = ?", (rs, rowNum) -> makeSubtask(rs), subtaskToDelete.getEpicId())
                .size() > 1) {
            String sql = "delete from subtasks where subtask_id = ?;";
            jdbcTemplate.update(sql, id);
        } else {
            String sql = "delete from subtasks where subtask_id = ?;" +
                    "delete from epics where epic_id =?";

            jdbcTemplate.update(sql, id, subtaskToDelete.getEpicId());
        }
        calculateEpicStatus(subtaskToDelete.getEpicId());
        updateTime(subtaskToDelete.getEpicId());
    }

    
    private Subtask makeSubtask(ResultSet rs) throws SQLException {
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

    private void calculateEpicStatus(int epicId){
        String sqlForEpicSubtasks = "select * from subtasks where epic_id = ?";
        Set<TaskStatus> stats = jdbcTemplate.query(sqlForEpicSubtasks, (rs, rowNum) -> makeSubtask(rs), epicId)
                .stream()
                .map(x -> x.getStatus())
                .collect(Collectors.toSet());

        String sqlToSetEpicStatus = "update epics set status=?::status where epic_id=?";

        if (stats.size() == 1 && stats.contains(TaskStatus.NEW)) {
           jdbcTemplate.update(sqlToSetEpicStatus, TaskStatus.NEW.toString(), epicId);
        } else if (stats.size() == 1 && stats.contains(TaskStatus.DONE)) {
            jdbcTemplate.update(sqlToSetEpicStatus, TaskStatus.DONE.toString(), epicId);
        } else {
            jdbcTemplate.update(sqlToSetEpicStatus, TaskStatus.IN_PROGRESS.toString(), epicId);
        }
    }

    private void updateTime(int epicId){

        Comparator<Subtask> comparator = new Comparator<Subtask>() {
            @Override
            public int compare(final Subtask o1, final Subtask o2) {

                if (o1.getStartTime().isBefore(o2.getStartTime())) {
                    return -1;
                } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        };

        List<Subtask> epicSubtasks = jdbcTemplate.query("select * from subtasks where epic_id=?", (rs, rowNum) -> makeSubtask(rs), epicId);
        if (epicSubtasks.isEmpty()) {
            return;
        }
        LocalDateTime startTime = epicSubtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(comparator)
                .get()
                .getStartTime();


        LocalDateTime endTime = epicSubtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .max(comparator)
                .get()
                .getEndTime();


        long duration = epicSubtasks.stream()
                .mapToLong(Subtask::getDuration)
                .sum();

        String sqlToSetEpicTime = "update epics set start_time=?, end_time=?, duration=? where epic_id=?";
        jdbcTemplate.update(sqlToSetEpicTime, startTime, endTime, duration, epicId);
    }
}
