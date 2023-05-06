package com.example.controller;

import com.example.model.Epic;
import com.example.model.Subtask;
import lombok.extern.slf4j.Slf4j;
/*import model.Epic;
import model.Subtask;*/
import com.example.model.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.service.TaskService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /*@GetMapping
    public List<Task> getPrioritizedTasks() {
        log.info("Получили список 10 самых приоритетных задач");
        return taskService.getPrioritizedTasks();
    }

    @GetMapping("/history")
    public List<Task> getHistory() {
        log.info("Получили историю действий персонала");
        return historyService.getHistory();
    }*/

    /** Functional for tasks */

    @GetMapping("/task")
    public List<Task> getAllTasks(){
        log.info("Получили список всех задач");
        return taskService.getAllTasks();
    }

    @GetMapping("/task/{id}")
    public Task getTaskById(@PathVariable int id) {
        log.info("Получили задачу с id{}", id);
        return taskService.getTaskById(id);
    }

    @PostMapping("/task")
    public Task createTask(@Valid  @RequestBody Task task) {
        Task newTask = taskService.createTask(task);
        log.info("Создали задачу с id{}", newTask.getId());
        return newTask;
    }

    @PutMapping("/task")
    public Task updateTask(@Valid @RequestBody Task task) {
        log.info("Обновили задачу с id{}", task.getId());
        return taskService.updateTask(task);
    }

    @DeleteMapping("/task")
    public void deleteAllTasks(){
        log.info("Удалили все задачи");
        taskService.deleteAllTasks();
    }

    @DeleteMapping("/task/{id}")
    public void deleteTaskById(@PathVariable int id) {
        log.info("Удалили задачу с id{}", id);
        taskService.deleteTaskById(id);
    }

    /** Functional for epics */

    @GetMapping("/epic")
    public List<Epic> getAllEpics(){
        log.info("Получили список всех эпиков");
        return taskService.getAllEpics();
    }

    @GetMapping("/epic/{id}")
    public Epic getEpicById(@PathVariable int id) {
        log.info("Получили эпик с id{}", id);
        return taskService.getEpicById(id);
    }

    /*
    @PostMapping("/epic")
    public Epic createEpic(@RequestBody Epic epic) {
        Epic newEpic = taskService.createNewEpic();
        log.info("Создали эпик с id{}", newEpic.getId());
        return taskService.createNewEpic();
    }

    @PutMapping("/epic/{id}")
    public Epic updateEpic(@PathVariable int id, Epic epic) {
        log.info("Обновили эпик с id{}", id);
        return taskService.updateEpic(id);
    }

    @DeleteMapping("/epic")
    public void deleteAllEpics(){
        log.info("Удалили все эпики");
        return taskService.deleteAllEpics();
    }

    @DeleteMapping("/epic/{id}")
    public void deleteEpicById(@PathVariable int id) {
        log.info("Удалили эпик с id{}", id);
        return taskService.deleteEpicById(id);
    }

    *//** Functional for subtasks */

    @GetMapping("/subtask")
    public List<Subtask> getAllSubtasks(){
        log.info("Получили все подзадачи");
        return taskService.getAllSubtasks();
    }

    @GetMapping("/subtask/{id}")
    public Subtask getSubtaskById(@PathVariable int id) {
        log.info("Получили подзадачу с id{}", id);
        return taskService.getSubtaskById(id);
    }

    /*
    @GetMapping("/subtask/epic/{id}")
    public List<Subtask> getEpicSubtaskById(@PathVariable int id) {
        log.info("Получили все подзадачи эпика с id{}", id);
        return taskService.getEpicSubtasksById(id);
    }

    @PostMapping("/subtask")
    public Subtask createSubtask(@RequestBody Subtask subtask) {
        Subtask newSubtask = taskService.createNewSubtask();
        log.info("Создали подзадачу с id{}", newSubtask.getId());
        return newSubtask;
    }

    @PutMapping("/subtask/{id}")
    public Subtask updateSubtask(@PathVariable int id, Subtask subtask) {
        log.info("Обновили подзадачу с id{}", id);
        return taskService.updateSubtask(id);
    }

    @DeleteMapping("/subtask")
    public void deleteAllSubtasks(){
        log.info("Удалили все подзадачи");
        return taskService.deleteAllSubtasks();
    }

    @DeleteMapping("/subtask/{id}")
    public void deleteSubtaskById(@PathVariable int id) {
        log.info("Удалили подзадачу с id{}", id);
        return taskService.deleteSubtaskById(id);
    }*/

}
