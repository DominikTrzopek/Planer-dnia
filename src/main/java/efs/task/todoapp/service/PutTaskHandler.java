package efs.task.todoapp.service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.repository.Task;
import efs.task.todoapp.repository.TaskEntity;
import efs.task.todoapp.web.HTTPCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class PutTaskHandler {

    Gson gson = new Gson();
    void writeToOs(TaskEntity t, OutputStream os) throws IOException {
        os.write(gson.toJson(t).getBytes());
    }
    public void handle(HttpExchange exchange, ToDoService service) throws IOException {

        DecodeBase64 decoder = new DecodeBase64();
        String[] str = decoder.decode(exchange);
        if (str == null) {
            return;
        }
        String username = str[0];
        String password = str[1];

        String[] url = exchange.getRequestURI().toString().split("/");
        String taskId = url[url.length - 1];

        String newTask = new BufferedReader(new InputStreamReader(exchange.getRequestBody()) )
                .lines().collect(Collectors.joining("\n"));

        Task taskConverted = service.convertToTask(newTask);
        if(taskConverted == null || taskConverted.description == null ||  taskConverted.description.isBlank() || username.isBlank()
                || (taskConverted.due != null && !service.validateDate(taskConverted.due)) || password.isBlank() || service.checkIfUserIsValid(username)) {
            exchange.sendResponseHeaders(HTTPCodes.BAD_REQUEST.getValue(), 0);
        }
        else if(!service.checkIfUserExists(username,password)) {
            exchange.sendResponseHeaders(HTTPCodes.USER_MISSING.getValue(), 0);
        }
        else if(taskId.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")){

            List<TaskEntity> task = service.getTaskFromId(taskId);
            if(task.size() == 0){
                exchange.sendResponseHeaders(HTTPCodes.NOT_FOUND.getValue(), 0);
            }
            else {
                if (task.get(0).user.equals(username)) {
                    TaskEntity updatedTask = service.updateTask(taskId, taskConverted.description,taskConverted.due,username);
                    exchange.sendResponseHeaders(HTTPCodes.OK.getValue(), 0);
                    OutputStream os = exchange.getResponseBody();
                    writeToOs(updatedTask, os);
                    os.close();
                }
                else {
                    exchange.sendResponseHeaders(HTTPCodes.FORBIDDEN.getValue(), 0);
                }
            }
        }
        else {
            exchange.sendResponseHeaders(HTTPCodes.NOT_FOUND.getValue(), 0);
        }

    }
}
