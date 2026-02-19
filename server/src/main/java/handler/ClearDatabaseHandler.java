package handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

/// Handler for the DELETE /db endpoint
public class ClearDatabaseHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        return;
    }
}
