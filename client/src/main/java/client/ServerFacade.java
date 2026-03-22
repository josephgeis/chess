package client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import request.*;
import response.*;

import java.net.http.HttpResponse;
import java.util.concurrent.Callable;
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

    record ErrorResponse(String message) { }

    public static abstract class ServerFacadeException extends Exception {
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

    public CompletableFuture<RegisterResponse> registerUserAsync(RegisterRequest request) {
        String data = gson.toJson(request);
        return makeAsyncRequest(() -> httpClient.post("/user", data), RegisterResponse.class);
    }

    public RegisterResponse registerUser(RegisterRequest request) throws ServerFacadeException {
        try {
            return registerUserAsync(request).join();
        } catch (CompletionException e) {
            throw (ServerFacadeException) e.getCause();
        }
    }

    public CompletableFuture<LoginResponse> loginUserAsync(LoginRequest request) {
        String data = gson.toJson(request);
        return makeAsyncRequest(() -> httpClient.post("/session", data), LoginResponse.class);
    }

    public LoginResponse loginUser(LoginRequest request) throws ServerFacadeException {
        try {
            return loginUserAsync(request).join();
        } catch (CompletionException e) {
            throw (ServerFacadeException) e.getCause();
        }
    }

    public CompletableFuture<Void> logoutUserAsync(String token) {
        return makeAsyncRequest(() -> httpClient.deleteAuthenticated("/session", token));
    }

    public void logoutUser(String token) throws ServerFacadeException {
        try {
            logoutUserAsync(token).join();
        } catch (CompletionException e) {
            throw (ServerFacadeException) e.getCause();
        }
    }

    public CompletableFuture<ListGamesResponse> listGamesAsync(String token) {
        return makeAsyncRequest(() -> httpClient.getAuthenticated("/game", token), ListGamesResponse.class);
    }

     public ListGamesResponse listGames(String token) throws ServerFacadeException {
        try {
            return listGamesAsync(token).join();
        } catch (CompletionException e) {
            throw (ServerFacadeException) e.getCause();
        }
    }

    public CompletableFuture<CreateGameResponse> createGameAsync(CreateGameRequest request, String token) {
        String data = gson.toJson(request);
        return makeAsyncRequest(() -> httpClient.postAuthenticated("/game", data, token), CreateGameResponse.class);
    }

    public CreateGameResponse createGame(CreateGameRequest request, String token) throws ServerFacadeException {
        try {
            return createGameAsync(request, token).join();
        } catch (CompletionException e) {
            throw (ServerFacadeException) e.getCause();
        }
    }

    public CompletableFuture<Void> joinGameAsync(JoinGameRequest request, String token) {
        String data = gson.toJson(request);
        return makeAsyncRequest(() -> httpClient.putAuthenticated("/game", data, token));
    }

    public void joinGame(JoinGameRequest request, String token) throws ServerFacadeException {
        try {
            joinGameAsync(request, token).join();
        } catch (CompletionException e) {
            throw (ServerFacadeException) e.getCause();
        }
    }

    public CompletableFuture<Void> clearDbAsync() {
        return makeAsyncRequest(() -> httpClient.delete("/db"));
    }

    public void clearDb() throws ServerFacadeException {
        try {
            clearDbAsync().join();
        } catch (CompletionException e) {
            throw (ServerFacadeException) e.getCause();
        }
    }

    private CompletableFuture<Void> makeAsyncRequest(Callable<CompletableFuture<HttpResponse<String>>> fn)
    {
        return makeAsyncRequest(fn, Void.TYPE);
    }

    private <T> CompletableFuture<T> makeAsyncRequest(Callable<CompletableFuture<HttpResponse<String>>> fn, Class<T> responseType) {
        CompletableFuture<T> responseFuture = new CompletableFuture<>();

        try {
            fn.call()
                    .thenAccept(res -> {
                        try {
                            responseFuture.complete(deserializeResponse(res, responseType));
                        } catch (ErrorResponseException e) {
                            responseFuture.completeExceptionally(e);
                        }
                    })
                    .exceptionally(throwable -> {
                        responseFuture.completeExceptionally(new RequestErrorException(throwable));
                        return null;
                    });
        } catch (Exception e) {
            responseFuture.completeExceptionally(new RequestErrorException(e));
        }

        return responseFuture;
    }


    private <T> T deserializeResponse(HttpResponse<String> res, Class<T> responseType) throws ErrorResponseException {
        try {
            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                ErrorResponse error = gson.fromJson(res.body(), ErrorResponse.class);
                throw new ErrorResponseException(error.message());
            }

            if (responseType == Void.TYPE) {
                return null;
            } else {
                return gson.fromJson(res.body(), responseType);
            }
        } catch (JsonSyntaxException e) {
            throw new ErrorResponseException("Server sent an unexpected response");
        }
    }
}
