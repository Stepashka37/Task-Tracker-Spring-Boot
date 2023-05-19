package com.example.service;

import com.example.dao.PrioritizedTaskDao;
import com.example.dao.TaskDao;
import com.example.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService extends AbstractService {
    private final TaskDao taskDao;
    private final PrioritizedTaskDao prioritizedTaskDao;

    @Autowired
    public TaskService(TaskDao taskDao, PrioritizedTaskDao prioritizedTaskDao) {
        this.taskDao = taskDao;
        this.prioritizedTaskDao = prioritizedTaskDao;
    }

    /** Task functionality */

    @Override
    public List<Task> getAll()  {
        return taskDao.getAll();
    }

    @Override
    public Task getById(int id) {
        return taskDao.getById(id);
    }

    @Override
    public Task create(Task task) {
        if (task.getDescription().isEmpty() || task.getDescription().isBlank()) {
            task.setDescription(task.getName());
        }
        return taskDao.create(task);
    }

    @Override
    public Task update(Task task) {
        return taskDao.update(task);
    }

    @Override
    public void deleteAll(){
        taskDao.deleteAll();
    }

    @Override
    public void deleteById(int id) {
        taskDao.deleteById(id);
    }

    @Override
    public List<Task> getPrioritized(String period, int count) {
        if (prioritizedTaskDao.getAll().size() < count) {
            count = prioritizedTaskDao.getAll().size();
        }
        return taskDao.getPrioritized(period, count);
    }


}
