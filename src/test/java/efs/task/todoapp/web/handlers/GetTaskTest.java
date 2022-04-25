package efs.task.todoapp.web.handlers;

import com.google.gson.Gson;
import efs.task.todoapp.repository.UUIDResponse;
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
public class GetTaskTest {
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

        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "something" );
        taskProperties.put("due", "2021-05-30" );
        String taskString = gson.toJson(taskProperties);
        var httpRequest1 = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();
        httpClient.send(httpRequest1, ofString());

    }

    @Test
    void shouldReturnOK_WhenGetOk() throws IOException, InterruptedException {
        //given
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .GET()
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.OK.getValue());
    }
    @Test
    void shouldReturnUserMissing_WhenUserMissing() throws IOException, InterruptedException {
        //given
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth", "dXNhbWV0YXNr:cGFzc3dvcmR0YXNr")
                .GET()
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.USER_MISSING.getValue());
    }
    @Test
    void shouldReturnUserMissing_WhenWrongPassword() throws IOException, InterruptedException {
        //given
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth", "dXNhbWV0YXNr:cGFzc3dvcmR0Yr")
                .GET()
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.USER_MISSING.getValue());
    }

    @Test
    void shouldReturnBadRequest_WhenHeaderMissing() throws IOException, InterruptedException {
        //given
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.BAD_REQUEST.getValue());
    }

    @Test
    void shouldReturnBadRequest_WhenAuthEmpty() throws IOException, InterruptedException {
        //given
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","")
                .GET()
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.BAD_REQUEST.getValue());
    }

    @Test
    void shouldReturnEmptyBody_WhenNoTasks() throws IOException, InterruptedException {
        //given

        Gson gson = new Gson();
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", "student" );
        userProperties.put("password", "haslo" );
        String userString = gson.toJson(userProperties);
        var httpRequestPostUser = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();


        var httpRequestGet = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","c3R1ZGVudA==:aGFzbG8=")
                .GET()
                .build();
        //when
        httpClient.send(httpRequestPostUser, ofString());
        var httpResponse = httpClient.send(httpRequestGet, ofString());

        //then
        assert(httpResponse.body().equals("[]"));
    }

    @Test
    void shouldReturnTasks_WhenMultipleTasks() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "something2");
        taskProperties.put("due", "2021-06-30");
        String taskString = gson.toJson(taskProperties);
        var httpRequestPostTask = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth", "dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();

        var httpRequestGet = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth", "dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .GET()
                .build();
        //when
        httpClient.send(httpRequestPostTask, ofString());
        var httpResponse = httpClient.send(httpRequestGet, ofString());

        //then
        assert(httpResponse.body().contains("something"));
        assert(httpResponse.body().contains("something2"));
    }

    @Test
    void shouldReturnNotFound_WhenGetIdWrong() throws IOException, InterruptedException {
        //given
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task/237e9877-e79b-12d4-a765-321741963000"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .GET()
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.NOT_FOUND.getValue());
    }

    @Test
    void shouldReturnTask_WhenGetIdOk() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "IdGiven" );
        taskProperties.put("due", "2041-10-30" );
        String taskString = gson.toJson(taskProperties);
        var httpRequestPostTask = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();

        var httpResponse1 = httpClient.send(httpRequestPostTask, ofString());
        String id = httpResponse1.body();
        UUIDResponse response = gson.fromJson(id,UUIDResponse.class);
        id = response.id;

        var httpRequestGet = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task/" + id))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .GET()
                .build();
        //when
        var httpResponse = httpClient.send(httpRequestGet, ofString());
        String returnedTask = httpResponse.body();

        //then
        assert(returnedTask.contains("IdGiven") && !returnedTask.contains("something"));
    }

    @Test
    void shouldReturnForbidden_WhenBelongsToOtherUser() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "IdGiven" );
        taskProperties.put("due", "2041-10-30" );
        String taskString = gson.toJson(taskProperties);
        var httpRequestPostTask = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();

        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", "Jan" );
        userProperties.put("password", "Jan" );
        String userString = gson.toJson(userProperties);
        var httpRequestPostUser = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();

        httpClient.send(httpRequestPostUser, ofString());
        var httpResponse1 = httpClient.send(httpRequestPostTask, ofString());

        String id = httpResponse1.body();
        UUIDResponse response = gson.fromJson(id,UUIDResponse.class);
        id = response.id;

        var httpRequestGet = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task/" + id))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","SmFu:SmFu")
                .GET()
                .build();
        //when
        var httpResponse = httpClient.send(httpRequestGet, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.FORBIDDEN.getValue());
    }

    @Test
    void shouldNotReturnUser_WhenGetOk() throws IOException, InterruptedException {
        //given
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .GET()
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());
        String body = httpResponse.body();
        //then
        assert(!body.contains("username"));
    }

}
