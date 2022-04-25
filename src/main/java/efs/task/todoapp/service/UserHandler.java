package efs.task.todoapp.service;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;


public class UserHandler implements HttpHandler {

    ToDoService service;

    public UserHandler(ToDoService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Auth");
        switch (exchange.getRequestMethod()){
            case "OPTION":
                exchange.sendResponseHeaders(200,0);
                break;
            case "POST":
                new PostUserHandler().handle(exchange,service);
                break;
        }
        exchange.close();
    }
}
