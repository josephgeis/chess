package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import request.*;
import result.*;
import service.ServiceManager;

public class Server {

    private final Javalin javalin;
    private final ServiceManager serviceManager;
    Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        serviceManager = ServiceManager.createPersistent();

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

    /// Automatically throws MalformedRequestException if deserialization fails.
    <R> R deserializeRequestBody(String body, Class<R> type) throws MalformedRequestException {
        R request;
        try {
            request = gson.fromJson(body, type);
        } catch (JsonSyntaxException e) {
            throw new MalformedRequestException();
        }

        return request;
    }

    /// Automatically pulls out the auth token and throws if not included.
    @NotNull
    private static String getAuthToken(@NotNull Context context) throws UnauthorizedRequestException {
        final String authToken = context.header("Authorization");
        if (authToken == null) {
            throw new UnauthorizedRequestException();
        }
        return authToken;
    }

    /// Handlers

    /// Handles the POST /user endpoint
    void handleRegister(@NotNull Context context) throws Exception {
        final RegisterRequest request = deserializeRequestBody(context.body(), RegisterRequest.class);

        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new MalformedRequestException();
        }

        RegisterResult result = serviceManager.getUserService().registerUser(request);
        context.json(gson.toJson(result));
    }

    /// Handles the POST /session endpoint
    void handleLogin(@NotNull Context context) throws Exception {
        final LoginRequest request = deserializeRequestBody(context.body(), LoginRequest.class);

        if (request.username() == null || request.password() == null) {
            throw new MalformedRequestException();
        }


        LoginResult result = serviceManager.getAuthService().loginUser(request);
        context.json(gson.toJson(result));
    }

    /// Handles the DELETE /session endpoint
    void handleLogout(@NotNull Context context) throws Exception {
        final String authToken = getAuthToken(context);

        serviceManager.getAuthService().logoutUser(authToken);

        context.json(gson.toJson(new Object()));
    }

    /// Handles the GET /game endpoint
    void handleListGames(@NotNull Context context) throws Exception {
        final String authToken = getAuthToken(context);

        ListGamesResult result = serviceManager.getGameService().listGames(authToken);

        context.json(gson.toJson(result));
    }

    /// Handles the POST /game endpoint
    void handleCreateGame(@NotNull Context context) throws Exception {
        final String authToken = getAuthToken(context);
        final CreateGameRequest request = deserializeRequestBody(context.body(), CreateGameRequest.class);

        if (request.gameName() == null) {
            throw new MalformedRequestException();
        }

        CreateGameResult result = serviceManager.getGameService().createGame(authToken, request);

        context.json(gson.toJson(result));
    }

    /// Handles the PUT /game endpoint
    void handleJoinGame(@NotNull Context context) throws Exception {
        final String authToken = getAuthToken(context);
        final JoinGameRequest request = deserializeRequestBody(context.body(), JoinGameRequest.class);

        if (request.playerColor() == null) {
            throw new MalformedRequestException();
        }

        serviceManager.getGameService().joinGame(authToken, request);

        context.json(gson.toJson(new Object()));
    }

    /// Handles the DELETE /db endpoint
    void handleClearDb(@NotNull Context context) throws Exception {
        serviceManager.clearDatabases();

        context.json(gson.toJson(new Object()));
    }

    void handleException(RequestException e, @NotNull Context context) {
        context.status(e.getStatusCode());
        context.json(e.responseAsJson());
    }
}
