package efs.task.todoapp.service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.repository.Task;
import efs.task.todoapp.repository.UUIDResponse;
import efs.task.todoapp.web.HTTPCodes;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

public class PostTaskHandler {
    Gson gson = new Gson();
    public void handle(HttpExchange exchange, ToDoService service) throws IOException {

        DecodeBase64 decoder = new DecodeBase64();
        String[] str = decoder.decode(exchange);
        if(str == null) {
            return;
        }
        String username = str[0];
        String password = str[1];
        String task = new BufferedReader(new InputStreamReader(exchange.getRequestBody()) )
                .lines().collect(Collectors.joining("\n"));

        Task taskConverted = service.convertToTask(task);
        if(taskConverted == null || taskConverted.description == null || taskConverted.description.isBlank()
                || (taskConverted.due != null && !service.validateDate(taskConverted.due)) || username.isBlank() || password.isBlank()|| service.checkIfUserIsValid(username)){
            exchange.sendResponseHeaders(HTTPCodes.BAD_REQUEST.getValue(), 0);
        }
        else if(!service.checkIfUserExists(username,password)) {
            exchange.sendResponseHeaders(HTTPCodes.USER_MISSING.getValue(), 0);
        }
        else {
            UUID feedback = service.saveTask(task,username);
            UUIDResponse response = new UUIDResponse();
            response.id = feedback.toString();
            exchange.sendResponseHeaders(HTTPCodes.CREATED.getValue(),0);
            OutputStream os = exchange.getResponseBody();
            os.write(gson.toJson(response).getBytes());
            os.close();
        }
    }
}

