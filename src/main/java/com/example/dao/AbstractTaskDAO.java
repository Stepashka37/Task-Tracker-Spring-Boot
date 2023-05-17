package com.example.dao;

import com.example.model.Task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class AbstractTaskDAO<T extends Task, E extends Task> {

    public abstract List<? extends Task> getAll();

    public abstract  <T extends Task> T  getById(int id);

    public abstract <T extends Task> T create( E e);

    public abstract <T extends Task> T update(E e);

    public abstract void deleteAll();

    public abstract void deleteById(int id);

}
