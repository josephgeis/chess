package server;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import request.*;
import response.*;
import service.ServiceManager;
import typeadapters.*;
import websocket.WebSocketController;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class Server {

    private final Javalin javalin;
    private final ServiceManager serviceManager;
    private final WebSocketController webSocketController;
    Gson gson;

    public Server(ServiceManager serviceManager) {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        this.gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(UserGameCommand.class,
                        new UserGameCommandAdapter())
                .registerTypeAdapter(ServerMessage.class,
                        new ServerMessageAdapter())
                .create();

        this.serviceManager = serviceManager;
        this.webSocketController = new WebSocketController(serviceManager.getDataAccessManager(), this.gson);

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::handleRegister)
                .post("/session", this::handleLogin)
                .delete("/session", this::handleLogout)
                .get("/game", this::handleListGames)
                .post("/game", this::handleCreateGame)
                .put("/game", this::handleJoinGame)
                .delete("/db", this::handleClearDb)
                .ws("/ws", this.webSocketController::initWs)
                .exception(Exception.class, this::handleException);
    }

    public Server() {
        this(ServiceManager.createPersistent());
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

        RegisterResponse result = serviceManager.getUserService().registerUser(request);
        context.json(gson.toJson(result));
    }

    /// Handles the POST /session endpoint
    void handleLogin(@NotNull Context context) throws Exception {
        final LoginRequest request = deserializeRequestBody(context.body(), LoginRequest.class);

        if (request.username() == null || request.password() == null) {
            throw new MalformedRequestException();
        }


        LoginResponse result = serviceManager.getAuthService().loginUser(request);
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

        ListGamesResponse result = serviceManager.getGameService().listGames(authToken);

        context.json(gson.toJson(result));
    }

    /// Handles the POST /game endpoint
    void handleCreateGame(@NotNull Context context) throws Exception {
        final String authToken = getAuthToken(context);
        final CreateGameRequest request = deserializeRequestBody(context.body(), CreateGameRequest.class);

        if (request.gameName() == null) {
            throw new MalformedRequestException();
        }

        CreateGameResponse result = serviceManager.getGameService().createGame(authToken, request);

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

    void handleException(Exception e, @NotNull Context context) {
        if (e instanceof RequestException) {
            context.status(((RequestException) e).getStatusCode());
            context.json((((RequestException) e).asJson(gson)));
        } else {
            System.err.println("Exception: " + e.getLocalizedMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                System.err.println(" - " + stackTraceElement.toString());
            }
            handleException(
                    new InternalServerError(e),
                    context
            );
        }
    }
}
