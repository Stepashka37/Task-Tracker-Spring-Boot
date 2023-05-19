package com.example.controller;

import com.example.model.Epic;
import com.example.model.Subtask;
import com.example.service.EpicService;
import com.example.service.SubtaskService;
import lombok.extern.slf4j.Slf4j;
/*import model.Epic;
import model.Subtask;*/
import com.example.model.Task;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.service.TaskService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/tasks")
@Validated
public class TaskController {

    private final TaskService taskService;
    private final EpicService epicService;
    private final SubtaskService subtaskService;

    public TaskController(TaskService taskService, EpicService epicService, SubtaskService subtaskService) {
        this.taskService = taskService;
        this.epicService = epicService;
        this.subtaskService = subtaskService;
    }

    @GetMapping("/priority")
    public List<Task> getPrioritizedTasks( @RequestParam String period, @RequestParam (defaultValue = "10") int count ) {
        log.info("Получили список 10 самых приоритетных задач");
        return taskService.getPrioritized(period, count);
    }

    /* @GetMapping("/history")
    public List<Task> getHistory() {
        log.info("Получили историю действий персонала");
        return historyService.getHistory();
    }*/

    /** FUNCTIONAL FOR TASKS */

    @GetMapping("/task")
    public List<Task> getAllTasks(){
        log.info("Получили список всех задач");
        return taskService.getAll();
    }

    @GetMapping("/task/{id}")
    public Task getTaskById(@PathVariable int id) {
        log.info("Получили задачу с id{}", id);
        return taskService.getById(id);
    }

    @PostMapping("/task")
    public Task createTask(@Valid @RequestBody Task task) {
        Task newTask = taskService.create(task);
        log.info("Создали задачу с id{}", newTask.getId());
        return newTask;
    }

    @PutMapping("/task")
    public Task updateTask(@Valid @RequestBody Task task) {
        log.info("Обновили задачу с id{}", task.getId());
        return taskService.update(task);
    }

    @DeleteMapping("/task")
    public void deleteAllTasks(){
        log.info("Удалили все задачи");
        taskService.deleteAll();
    }

    @DeleteMapping("/task/{id}")
    public void deleteTaskById(@PathVariable int id) {
        log.info("Удалили задачу с id{}", id);
        taskService.deleteById(id);
    }

    /** FUNCTIONAL FOR EPICS */

    @GetMapping("/epic")
    public List<Epic> getAllEpics(){
        log.info("Получили список всех эпиков");
        return epicService.getAll();
    }

    @GetMapping("/epic/{id}")
    public Epic getEpicById(@PathVariable int id) {
        log.info("Получили эпик с id{}", id);
        return epicService.getById(id);
    }


    @PostMapping("/epic")
    public Epic createEpic(@Valid @RequestBody Epic epic) {
        Epic newEpic = epicService.create(epic);
        log.info("Создали эпик с id{}", newEpic.getId());
        return newEpic;
    }

    @PutMapping("/epic")
    public Epic updateEpic(@Valid @RequestBody Epic epic) {
        Epic epicUpd = epicService.update(epic);
        log.info("Обновили эпик с id{}", epic.getId());
        return epicUpd;
    }

    @DeleteMapping("/epic")
    public void deleteAllEpics(){
        log.info("Удалили все эпики");
       epicService.deleteAll();
    }

    @DeleteMapping("/epic/{id}")
    public void deleteEpicById(@PathVariable int id) {
        log.info("Удалили эпик с id{}", id);
        epicService.deleteById(id);
    }

    /** FUNCTIONAL FOR SUBTASKS */

    @GetMapping("/subtask")
    public List<Subtask> getAllSubtasks(){
        log.info("Получили все подзадачи");
        return subtaskService.getAll();
    }

    @GetMapping("/subtask/{id}")
    public Subtask getSubtaskById(@PathVariable int id) {
        log.info("Получили подзадачу с id{}", id);
        return subtaskService.getById(id);
    }

    @GetMapping("/epic/subtasks")
    public List<Subtask> getEpicSubtaskById(@RequestParam int id) {
        log.info("Получили все подзадачи эпика с id{}", id);
        return subtaskService.getEpicSubtasks(id);
    }


    @PostMapping("/subtask")
    public Subtask createSubtask(@Valid @RequestBody Subtask subtask) {
        Subtask newSubtask = subtaskService.create(subtask);
        log.info("Создали подзадачу с id{}", newSubtask.getId());
        return newSubtask;
    }

    @PutMapping("/subtask")
    public Subtask updateSubtask(@Valid @RequestBody Subtask subtask) {
        Subtask subtaskUpd = subtaskService.update(subtask);
        log.info("Обновили подзадачу с id{}", subtask.getId());
        return subtaskUpd;
    }

    @DeleteMapping("/subtask")
    public void deleteAllSubtasks(){
        log.info("Удалили все подзадачи");
        subtaskService.deleteAll();
    }

    @DeleteMapping("/subtask/{id}")
    public void deleteSubtaskById(@PathVariable int id) {
        log.info("Удалили подзадачу с id{}", id);
        subtaskService.deleteById(id);
    }

}
