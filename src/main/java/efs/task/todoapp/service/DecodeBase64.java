package efs.task.todoapp.service;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import efs.task.todoapp.web.HTTPCodes;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class DecodeBase64 {
    public String[] decode(HttpExchange exchange) throws IOException {
        try {
            Headers headers = exchange.getRequestHeaders();
            List<String> userList = headers.get("auth");
            if (userList == null)
                throw new IllegalArgumentException();
            String encodedUser = userList.get(0);
            if (encodedUser.isBlank() || !encodedUser.contains(":")){
                throw new IllegalArgumentException();
            }
            String[] user = encodedUser.split(":");

            byte[] decoded = Base64.getDecoder().decode(user[0]);
            String username = new String(decoded);

            decoded = Base64.getDecoder().decode(user[1]);
            String password = new String(decoded);

            return new String[]{username,password};
        }
        catch (IllegalArgumentException | NullPointerException ex){
            exchange.sendResponseHeaders(HTTPCodes.BAD_REQUEST.getValue(), 0);
            return null;
        }
    }
}
