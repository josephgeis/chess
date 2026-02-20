package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import request.RegisterRequest;
import result.RegisterResult;
import service.ServiceManager;

public class Server {

    private final Javalin javalin;
    private final ServiceManager serviceManager;
    Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        serviceManager = new ServiceManager();

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::handleRegister)
                .post("/session", this::handleLogin)
                .delete("/session", this::handleLogout)
                .get("/game", this::handleListGames)
                .post("/game", this::handleCreateGame)
                .put("/game", this::handleJoinGame)
                .delete("/db", this::handleClearDb)
                .exception(RequestException.class, this::handleException);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    /// Handlers

    /// Handles the POST /user endpoint
    void handleRegister(@NotNull Context context) throws Exception {
        RegisterRequest request;
        try {
            request = gson.fromJson(context.body(), RegisterRequest.class);
        } catch (JsonSyntaxException e) {
            throw new MalformedRequestException();
        }

        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new MalformedRequestException();
        }

        RegisterResult result = serviceManager.getUserService().registerUser(request);
        context.json(gson.toJson(result));
    }

    /// Handles the POST /session endpoint
    void handleLogin(@NotNull Context context) throws Exception { }

    /// Handles the DELETE /session endpoint
    void handleLogout(@NotNull Context context) throws Exception { }

    /// Handles the GET /game endpoint
    void handleListGames(@NotNull Context context) throws Exception { }

    /// Handles the POST /game endpoint
    void handleCreateGame(@NotNull Context context) throws Exception { }

    /// Handles the PUT /game endpoint
    void handleJoinGame(@NotNull Context context) throws Exception { }

    /// Handles the DELETE /db endpoint
    void handleClearDb(@NotNull Context context) throws Exception { }

    void handleException(RequestException e, @NotNull Context context) {
        context.status(e.getStatusCode());
        context.json(e.responseAsJson());
    }
}
