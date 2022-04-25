package efs.task.todoapp.repository;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskRepository implements Repository<UUID, TaskEntity> {

    Map<UUID,TaskEntity> tasks = new HashMap<>();
    @Override
    public UUID save(TaskEntity taskEntity) {
        TaskEntity previous =  tasks.putIfAbsent(taskEntity.id,taskEntity);
        if(previous != null)
        {
            return null;
        }
        return taskEntity.id;
    }

    @Override
    public TaskEntity query(UUID uuid) {
        return tasks.get(uuid);
    }

    @Override
    public List<TaskEntity> query(Predicate<TaskEntity> condition) {
        return tasks.values()
                .stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    @Override
    public TaskEntity update(UUID uuid, TaskEntity taskEntity) {
        try {
            TaskEntity previous = tasks.replace(uuid, taskEntity);
        }
        catch (NullPointerException err) {
            return null;
        }
        return taskEntity;
    }

    @Override
    public boolean delete(UUID uuid) {
        TaskEntity removed = tasks.remove(uuid);
        return removed != null;
    }
}
