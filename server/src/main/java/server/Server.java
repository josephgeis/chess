package server;

import handler.*;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", new ClearDatabaseHandler())
                .post("/user", new CreateUserHandler())
                .post("/session", new LoginUserHandler())
                .delete("/session", new LogoutUserHandler())
                .get("/game", new ListGamesHandler())
                .post("/game", new CreateGameHandler())
                .put("/game", new JoinGameHandler());

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
