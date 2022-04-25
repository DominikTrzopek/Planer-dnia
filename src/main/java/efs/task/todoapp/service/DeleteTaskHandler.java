package efs.task.todoapp.service;

import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.repository.TaskEntity;
import efs.task.todoapp.web.HTTPCodes;
import java.io.IOException;
import java.util.List;

public class DeleteTaskHandler {

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

        if (username.isBlank() || password.isBlank() || service.checkIfUserIsValid(username)) {
            exchange.sendResponseHeaders(HTTPCodes.BAD_REQUEST.getValue(), 0);
        } else if (!service.checkIfUserExists(username, password)) {
            exchange.sendResponseHeaders(HTTPCodes.USER_MISSING.getValue(), 0);
        } else if (taskId.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            List<TaskEntity> task = service.getTaskFromId(taskId);
            if (task.size() == 0) {
                exchange.sendResponseHeaders(HTTPCodes.NOT_FOUND.getValue(), 0);
            } else {
                if (task.get(0).user.equals(username) && service.deleteTask(taskId)) {
                    exchange.sendResponseHeaders(HTTPCodes.OK.getValue(), 0);
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
