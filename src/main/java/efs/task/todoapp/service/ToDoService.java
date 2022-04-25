package efs.task.todoapp.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import efs.task.todoapp.repository.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

public class ToDoService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ToDoService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public boolean checkIfUserExists(String username, String password){
        UserEntity user = userRepository.query(username);
        return user != null && user.password.equals(password);
    }

    public List<TaskEntity> getTasks(String username){
        return taskRepository.query(t -> t.user.equals(username));
    }

    public List<TaskEntity> getTaskFromId(String id){
        return taskRepository.query(t -> t.id.toString().equals(id));
    }

    public boolean deleteTask(String id){
        return taskRepository.delete(UUID.fromString(id));
    }

    public UserEntity convertToUser(String user){
        try {
            return new Gson().fromJson(user, UserEntity.class);
        }
        catch (JsonSyntaxException ex){
            return null;
        }
    }

    public Task convertToTask(String task){
        try {
            return new Gson().fromJson(task, Task.class);
        }
        catch (JsonSyntaxException ex){
            return null;
        }
    }

    public TaskEntity updateTask(String id, String description, String due, String user){
        TaskEntity task = new TaskEntity(description,due,user);
        task.id = UUID.fromString(id);
        return taskRepository.update(task.id,task);
    }

    public UUID saveTask(String task, String user){
        Task taskConverted = convertToTask(task);
        TaskEntity taskEntity = new TaskEntity(taskConverted.description,taskConverted.due,user);
        return taskRepository.save(taskEntity);
    }

    public String saveUser(String userString) {
        return userRepository.save(convertToUser(userString));
    }

    boolean checkIfUserIsValid(String s){
        return !s.matches("[a-zA-Z0-9]*");
    }

    final static String DATE_FORMAT = "yyyy-MM-dd";
    boolean validateDate(String date){
        try {
            if(!date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")){
                return false;
            }
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
