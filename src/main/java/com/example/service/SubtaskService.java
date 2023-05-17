package com.example.service;

import com.example.dao.SubtaskDao;
import com.example.dao.SubtaskDao;
import com.example.model.Subtask;
import com.example.model.Subtask;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubtaskService extends AbstractService<Subtask, Subtask> {
    private final SubtaskDao subtaskDao;

    public SubtaskService(SubtaskDao subtaskDao) {
        this.subtaskDao = subtaskDao;
    }

    @Override
    public List<Subtask> getAll()  {
        return subtaskDao.getAll();
    }

    @Override
    public Subtask getById(int id) {
        return subtaskDao.getById(id);
    }

    @Override
    public Subtask create(Subtask subtask) {
        if (subtask.getDescription().isEmpty() || subtask.getDescription().isBlank()) {
            subtask.setDescription(subtask.getName());
        }
        return subtaskDao.create(subtask);
    }

    @Override
    public Subtask update(Subtask subtask) {
        return subtaskDao.update(subtask);
    }

    @Override
    public void deleteAll(){
        subtaskDao.deleteAll();
    }

    @Override
    public void deleteById(int id) {
        subtaskDao.deleteById(id);
    }

    public List<Subtask> getEpicSubtasks(int id){
        return subtaskDao.getEpicSubtasks(id);
    }
}
