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
public class UserHandlerTest {

    public static final String TODO_APP_PATH = "http://localhost:8080/todo/";

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
    }

    @Test
    void shouldReturnCreated_WhenUserCreated() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", "username" + System.currentTimeMillis() );
        userProperties.put("password", "password" + System.currentTimeMillis() );
        String userString = gson.toJson(userProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.CREATED.getValue());
    }

    @Test
    void shouldReturnExists_WhenSameUserCreated() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", "username1" );
        userProperties.put("password", "password1" );
        String userString = gson.toJson(userProperties);
        var httpRequest1 = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();
        userProperties.put("username", "username1" );
        userProperties.put("password", "password1" );
        userString = gson.toJson(userProperties);
        var httpRequest2 = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();

        //when
        httpClient.send(httpRequest1, ofString());
        var httpResponse2 = httpClient.send(httpRequest2, ofString());


        //then
        assertThat(httpResponse2.statusCode()).as("Response status code").isEqualTo(HTTPCodes.EXISTS.getValue());
    }

    @Test
    void shouldReturnBadRequest_WhenUserWithNoNameCreated() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", "  " );
        userProperties.put("password", "pass" );
        String userString = gson.toJson(userProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.BAD_REQUEST.getValue());
    }

    @Test
    void shouldReturnBadRequest_WhenUserWithNoPasswordCreated() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", "user" );
        userProperties.put("password", "  " );
        String userString = gson.toJson(userProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.BAD_REQUEST.getValue());
    }

    @Test
    void shouldReturnBadRequest_WhenSpecialSymbolsUsed() throws IOException, InterruptedException {
        //given
        Gson gson = new Gson();
        Map<String, String> userProperties = new HashMap<>();
        userProperties.put("username", "a,r" );
        userProperties.put("password", "passw" );
        String userString = gson.toJson(userProperties);
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(TODO_APP_PATH + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(userString))
                .build();

        //when
        var httpResponse = httpClient.send(httpRequest, ofString());

        //then
        assertThat(httpResponse.statusCode()).as("Response status code").isEqualTo(HTTPCodes.BAD_REQUEST.getValue());
    }



}


