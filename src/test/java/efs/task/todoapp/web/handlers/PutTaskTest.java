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
public class PutTaskTest {
    public static final String TODO_APP_PATH = "http://localhost:8080/todo/";
    String id;
    private HttpClient httpClient;
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        httpClient = HttpClient.newHttpClient();

        Gson gson = new Gson();
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", "usernametask" );
        userProperties.put("password", "passwordtask" );
        String userString = gson.toJson(userProperties);
        var httpRequestPostUser = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();
        httpClient.send(httpRequestPostUser, ofString());

        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "something" );
        taskProperties.put("due", "2021-05-30" );
        String taskString = gson.toJson(taskProperties);
        var httpRequestPostTask = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .POST(HttpRequest.BodyPublishers.ofString(taskString))
                .build();

        var httpResponse1 = httpClient.send(httpRequestPostTask, ofString());

        id = httpResponse1.body();
        UUIDResponse response = gson.fromJson(id,UUIDResponse.class);
        id = response.id;

    }

    @Test
    void shouldReturnOk_WhenTaskUpdated() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "update" );
        taskProperties.put("due", "2021-12-30" );
        String updatedTaskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task/" + id))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedTaskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.OK.getValue());
    }

    @Test
    void shouldReturnBadRequest_WhenEmptyDescription() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "" );
        taskProperties.put("due", "2021-06-30" );
        String updatedTaskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task/" + id))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedTaskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.BAD_REQUEST.getValue());
    }

    @Test
    void shouldReturnBadRequest_WhenWrongDate() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "fff" );
        taskProperties.put("due", "2021-41-02" );
        String updatedTaskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task/" + id))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedTaskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.BAD_REQUEST.getValue());
    }

    @Test
    void shouldReturnForbidden_WhenBelongsToOtherUser() throws IOException, InterruptedException {
        //given

        Gson gson = new Gson();
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", "Jan" );
        userProperties.put("password", "Jan" );
        String userString = gson.toJson(userProperties);
        var httpRequestPostUser = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();

        Map<String, String> updatedTaskProperties = new HashMap<>();
        updatedTaskProperties.put("description", "update" );
        updatedTaskProperties.put("due", "2021-06-30" );
        String updatedTaskString = gson.toJson(updatedTaskProperties);
        var httpRequestPut = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task/" + id))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","SmFu:SmFu")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedTaskString))
                .build();
        //when
        httpClient.send(httpRequestPostUser, ofString());
        var httpResponse = httpClient.send(httpRequestPut, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.FORBIDDEN.getValue());
    }

    @Test
    void shouldReturnNotFound_WhenWrongId() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "update" );
        taskProperties.put("due", "2021-06-30" );
        String updatedTaskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task/" + "4234"))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvcmR0YXNr")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedTaskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.NOT_FOUND.getValue());
    }

    @Test
    void shouldReturnUserMissing_WhenWrongPassword() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> taskProperties = new HashMap<>();
        taskProperties.put("description", "update" );
        taskProperties.put("due", "2021-06-30" );
        String updatedTaskString = gson.toJson(taskProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "task/" + id))
                .version(HttpClient.Version.HTTP_1_1)
                .setHeader("auth","dXNlcm5hbWV0YXNr:cGFzc3dvNr")
                .PUT(HttpRequest.BodyPublishers.ofString(updatedTaskString))
                .build();
        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.USER_MISSING.getValue());
    }
}
