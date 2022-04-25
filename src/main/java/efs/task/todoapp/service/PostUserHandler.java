package efs.task.todoapp.service;

import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.repository.UserEntity;
import efs.task.todoapp.web.HTTPCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

public class PostUserHandler {

    public void handle(HttpExchange exchange, ToDoService service) throws IOException {

        String user = new BufferedReader(new InputStreamReader(exchange.getRequestBody()) )
                .lines().collect(Collectors.joining("\n"));

        String feedback = null;
        String[] validate = user.split(",");
        UserEntity userEntity = service.convertToUser(user);
        if(userEntity == null || validate.length != 2 || userEntity.username.isBlank() || userEntity.password.isBlank()
                || service.checkIfUserIsValid(userEntity.username) ) {
            exchange.sendResponseHeaders(HTTPCodes.BAD_REQUEST.getValue(), 0);
        }
        else {
            feedback = service.saveUser(user);
            if (feedback == null) {
                exchange.sendResponseHeaders(HTTPCodes.EXISTS.getValue(), 0);
            } else {
                exchange.sendResponseHeaders(HTTPCodes.CREATED.getValue(), feedback.length());
            }
        }
        OutputStream os = exchange.getResponseBody();
        if (feedback != null)
            os.write(feedback.getBytes());
        os.close();

    }
}
