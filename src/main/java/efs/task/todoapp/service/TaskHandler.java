package efs.task.todoapp.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;



public class TaskHandler implements HttpHandler {

    ToDoService service;

    public TaskHandler(ToDoService service)  {
        this.service = service;
    }

    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Auth");
        switch (exchange.getRequestMethod()){
            case "OPTION":
                exchange.sendResponseHeaders(200,0);
                break;
            case "GET":
                new GetTaskHandler().handle(exchange,service);
                break;
            case "PUT":
                new PutTaskHandler().handle(exchange,service);
                break;
            case "DELETE":
                new DeleteTaskHandler().handle(exchange,service);
                break;
            case "POST":
                new PostTaskHandler().handle(exchange,service);
        }
        exchange.close();
    }
}
