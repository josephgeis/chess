package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

/// Handles the DELETE /session endpoint
public class LogoutUserHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        return;
    }
}
