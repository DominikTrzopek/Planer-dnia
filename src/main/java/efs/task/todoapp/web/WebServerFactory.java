package efs.task.todoapp.web;

import com.sun.net.httpserver.HttpServer;
import efs.task.todoapp.repository.TaskRepository;
import efs.task.todoapp.repository.UserRepository;
import efs.task.todoapp.service.TaskHandler;
import efs.task.todoapp.service.ToDoService;
import efs.task.todoapp.service.UserHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServerFactory {
    static ToDoService service = new ToDoService(new UserRepository(), new TaskRepository());
    public static HttpServer createServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);
        server.createContext("/todo/user",new UserHandler(service));
        server.createContext("/todo/task",new TaskHandler(service));
        return server;
    }
}
