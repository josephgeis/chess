package client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import request.*;
import response.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ServerFacade {
    String host;
    int port;

    ChessHttpClient httpClient;

    Gson gson;

    public ServerFacade(String host, int port) {
        this.host = host;
        this.port = port;
        this.httpClient = new ChessHttpClient(host, port);
        gson = new Gson();
    }

    record ErrorResponse(String message) { };

    public static class ServerFacadeException extends Exception {
        public ServerFacadeException(String message) {
            super(message);
        }

        public ServerFacadeException(Throwable cause) {
            super(cause);
        }
    }

    public static class ErrorResponseException extends ServerFacadeException {
        public ErrorResponseException(String message) {
            super(message);
        }
    }

    public static class RequestErrorException extends ServerFacadeException {
        public RequestErrorException(Throwable cause) {
            super(cause);
        }
    }

    public RegisterResponse registerUser(RegisterRequest request) throws ServerFacadeException {
        String data = gson.toJson(request);

        HttpResponse<String> res;
        try {
            res = httpClient.post("/user", data).join();
        } catch (CompletionException e) {
            throw new RequestErrorException(e.getCause());
        } catch (Exception e) {
            throw new ServerFacadeException(e);
        }

        return deserializeResponse(res, RegisterResponse.class);
    }

    public LoginResponse loginUser(LoginRequest request) {
        return new LoginResponse("joseph", "fakeToken");
    }

    public void logoutUser() { }

    public ListGamesResponse listGames() {
        return new ListGamesResponse(Collections.emptyList());
    }

    public CreateGameResponse createGame(CreateGameRequest request) {
        return new CreateGameResponse(0);
    }

    public void joinGame(JoinGameRequest request) { }

    public void clearDb() { }

    private <T> T deserializeResponse(HttpResponse<String> res, Class<T> responseType) throws ErrorResponseException {
        try {
            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                ErrorResponse error = gson.fromJson(res.body(), ErrorResponse.class);
                throw new ErrorResponseException(error.message());
            }

            return gson.fromJson(res.body(), responseType);
        } catch (JsonSyntaxException e) {
            throw new ErrorResponseException("Server sent an unexpected response");
        }
    }
}
