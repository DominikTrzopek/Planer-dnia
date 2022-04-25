package efs.task.todoapp.web.handlers;

import com.google.gson.Gson;
import efs.task.todoapp.util.ToDoServerExtension;
import efs.task.todoapp.web.HTTPCodes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(ToDoServerExtension.class)
public class PostTaskTest {
    public static final String TODO_APP_PATH = "http://localhost:8080/todo/";

    private HttpClient httpClient;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        httpClient = HttpClient.newHttpClient();

        Gson gson = new Gson();
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", "usernametask" );
        userProperties.put("password", "passwordtask" );
        String userString = gson.toJson(userProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();
        httpClient.send(httpRequest, ofString());
    }

    @Test
    void shouldReturnCreated_WhenTaskCreated() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "something" );
        taskProperties.put("due", "2021-06-30" );
        String taskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.CREATED.getValue());
    }

    @Test
    void shouldReturnCreated_WhenTaskCreatedNoDate() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "something" );
        String taskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.CREATED.getValue());
    }

    @Test
    void shouldReturnBadRequest_WhenTaskDescriptionEmpty() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", " " );
        taskProperties.put("due", "2020-06-30" );
        String taskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.BAD_REQUEST.getValue());
    }

    @Test
    void shouldReturnBadRequest_WhenTaskDateIncorrect() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "wololo" );
        taskProperties.put("due", "2021-30-05" );
        String taskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.BAD_REQUEST.getValue());
    }

    @Test
    void shouldReturnBadRequest_WhenAuthIncorrect() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "abc" );
        taskProperties.put("due", "2021-30-05" );
        String taskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","csadsadad")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.BAD_REQUEST.getValue());
    }

    @Test
    void shouldReturnUserMissing_WhenPasswordIncorrect() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "abc" );
        taskProperties.put("due", "2021-03-05" );
        String taskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:dXNlcm5r")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.USER_MISSING.getValue());
    }


}
