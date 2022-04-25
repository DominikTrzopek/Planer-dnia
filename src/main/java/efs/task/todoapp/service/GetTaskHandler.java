package efs.task.todoapp.service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.repository.TaskEntity;
import efs.task.todoapp.web.HTTPCodes;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class GetTaskHandler {

    Gson gson = new Gson();
    void writeToOs(TaskEntity t, OutputStream os) throws IOException {
        os.write(gson.toJson(t).getBytes());
    }

    public void handle(HttpExchange exchange, ToDoService service) throws IOException {

        String[] url = exchange.getRequestURI().toString().split("/");
        String taskId = url[url.length - 1];

        DecodeBase64 decoder = new DecodeBase64();
        String[] str = decoder.decode(exchange);
        if (str == null) {
            return;
        }
        String username = str[0];
        String password = str[1];
        if(username.isBlank() || password.isBlank()|| service.checkIfUserIsValid(username)){
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
                Boolean ok = false;
                List<TaskEntity> userTasks = service.getTasks(username);
                for(TaskEntity t : userTasks) {
                    if (t.id.toString().equals(taskId)) {
                        exchange.sendResponseHeaders(HTTPCodes.OK.getValue(), 0);
                        OutputStream os = exchange.getResponseBody();
                        writeToOs(t, os);
                        os.close();
                        ok = true;
                    }
                }
                if(ok.equals(false))
                    exchange.sendResponseHeaders(HTTPCodes.FORBIDDEN.getValue(), 0);
                }
        }
        else {
            List<TaskEntity> tasks = service.getTasks(username);
            exchange.sendResponseHeaders(HTTPCodes.OK.getValue(), 0);
            List<TaskEntity> list = new ArrayList<>(tasks);
            OutputStream os = exchange.getResponseBody();
            String response = gson.toJson(list,list.getClass());
            os.write(response.getBytes());
            os.close();
       }
    }
}
