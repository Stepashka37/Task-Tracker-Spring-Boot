package com.example.service;

import com.example.dao.TaskDao;
import com.example.model.Epic;
import com.example.model.Subtask;
import com.example.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskDao taskDao;

    @Autowired
    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    /** Task functionality */

    public List<Task> getAllTasks()  {
        return taskDao.getAllTasks();
    }

    public Task getTaskById(int id) {
        return taskDao.getTaskById(id);
    }

    public Task createTask(Task task) {
        if (task.getDescription().isEmpty() || task.getDescription().isBlank()) {
            task.setDescription(task.getName());
        }
        return taskDao.createNewTask(task);
    }

    public Task updateTask(Task task) {
        return taskDao.updateTask(task);
    }

    public void deleteAllTasks(){
        taskDao.deleteAllTasks();
    }

    public void deleteTaskById(int id) {
        taskDao.deleteTaskById(id);
    }

    /** Epic functionality */

    public List<Epic> getAllEpics()  {
        return taskDao.getAllEpics();
    }

    public Epic getEpicById(int id) {
        return taskDao.getEpicById(id);
    }

    public List<Subtask> getEpicSubtasks(int id){
        return taskDao.getEpicSubtasks(id);
    }

    public Epic createEpic(Epic epic) {
        if (epic.getDescription().isEmpty() || epic.getDescription().isBlank()) {
            epic.setDescription(epic.getName());
        }
        return taskDao.createNewEpic(epic);
    }

    public Epic updateEpic(Epic epic) {
        return taskDao.updateEpic(epic);
    }

    public void deleteAllEpics(){
        taskDao.deleteAllEpics();
    }

    public void deleteEpicById(int id) {
        taskDao.deleteEpicById(id);
    }

    /** Subtask functionality */

    public List<Subtask> getAllSubtasks()  {
        return taskDao.getAllSubtasks();
    }

    public Subtask getSubtaskById(int id) {
        return taskDao.getSubtaskById(id);
    }

    public Subtask createSubtask(Subtask subtask) {
        if (subtask.getDescription().isEmpty() || subtask.getDescription().isBlank()) {
            subtask.setDescription(subtask.getName());
        }
        taskDao.getEpicById(subtask.getEpicId());
        return taskDao.createNewSubtask(subtask);
    }

    public Subtask updateSubtask(Subtask subtask) {
        taskDao.getEpicById(subtask.getEpicId());
        return taskDao.updateSubtask(subtask);
    }

    public void deleteAllSubtasks(){
        taskDao.deleteAllSubtasks();
    }

    public void deleteSubtaskById(int id) {
        taskDao.deleteSubtaskById(id);
    }


}
