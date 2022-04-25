package efs.task.todoapp.repository;

import java.util.UUID;

public class TaskEntity {

    public TaskEntity(String description_t, String due_t, String username){
        description = description_t;
        due = due_t;
        id = UUID.randomUUID();
        user = username;
    }

    public UUID id;
    public String description;
    public String due;
    public transient String user;
}