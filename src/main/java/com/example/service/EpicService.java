package com.example.service;

import com.example.dao.AbstractTaskDAO;
import com.example.dao.EpicDao;
import com.example.model.Epic;
import com.example.model.Subtask;
import com.example.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpicService extends AbstractTaskDAO<Epic, Epic> {
    private final EpicDao epicDao;

    public EpicService(EpicDao epicDao) {
        this.epicDao = epicDao;
    }

    @Override
    public List<Epic> getAll()  {
        return epicDao.getAll();
    }

    @Override
    public Epic getById(int id) {
        return epicDao.getById(id);
    }

    @Override
    public Epic create(Epic epic) {
        if (epic.getDescription().isEmpty() || epic.getDescription().isBlank()) {
            epic.setDescription(epic.getName());
        }
        return epicDao.create(epic);
    }

    @Override
    public Epic update(Epic epic) {
        return epicDao.update(epic);
    }

    @Override
    public void deleteAll(){
        epicDao.deleteAll();
    }

    @Override
    public void deleteById(int id) {
        epicDao.deleteById(id);
    }

    @Override
    public List<Epic> getPrioritized(String period, int count) {
        return null;
    }
}
