package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import request.RegisterRequest;

/// Handler for the POST /user endpoint
public class RegisterHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        Gson gson = new Gson();

        RegisterRequest request;
        try {
            request = gson.fromJson(context.body(), RegisterRequest.class);
        } catch (JsonSyntaxException e) {
            throw new MalformedRequestException();
        }

        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new MalformedRequestException();
        }


    }
}
