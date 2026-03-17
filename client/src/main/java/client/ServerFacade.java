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

    public CompletableFuture<RegisterResponse> registerUserAsync(RegisterRequest request) throws ServerFacadeException {
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

    public CompletableFuture<LoginResponse> loginUserAsync(LoginRequest request) throws ServerFacadeException {
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

    public void logoutUser(String token) throws ServerFacadeException {
        HttpResponse<String> res;
        try {
            res = httpClient.deleteAuthenticated("/session", token).join();
        } catch (CompletionException e) {
            throw new RequestErrorException(e.getCause());
        } catch (Exception e) {
            throw new RequestErrorException(e);
        }

        deserializeResponse(res, Object.class);
    }

     public ListGamesResponse listGames(String token) throws ServerFacadeException {
        HttpResponse<String> res;
        try {
            res = httpClient.getAuthenticated("/game", token).join();
        } catch (CompletionException e) {
            throw new RequestErrorException(e.getCause());
        } catch (Exception e) {
            throw new RequestErrorException(e);
        }

        return deserializeResponse(res, ListGamesResponse.class);
    }

    public CreateGameResponse createGame(CreateGameRequest request, String token) throws ServerFacadeException {
        String data = gson.toJson(request);

        HttpResponse<String> res;
        try {
            res = httpClient.postAuthenticated("/game", data, token).join();
        } catch (CompletionException e) {
            throw new RequestErrorException(e.getCause());
        } catch (Exception e) {
            throw new RequestErrorException(e);
        }

        return deserializeResponse(res, CreateGameResponse.class);
    }

    public void joinGame(JoinGameRequest request, String token) throws ServerFacadeException {
        String data = gson.toJson(request);

        HttpResponse<String> res;
        try {
            res = httpClient.putAuthenticated("/game", data, token).join();
        } catch (CompletionException e) {
            throw new RequestErrorException(e.getCause());
        } catch (Exception e) {
            throw new RequestErrorException(e);
        }

        deserializeResponse(res, Object.class);
    }

    public void clearDb() throws ServerFacadeException {
        HttpResponse<String> res;
        try {
            res = httpClient.delete("/db").join();
        } catch (CompletionException e) {
            throw new RequestErrorException(e.getCause());
        } catch (Exception e) {
            throw new RequestErrorException(e);
        }

        deserializeResponse(res, Object.class);
    }

    private <T extends Record> CompletableFuture<T> makeAsyncRequest(Callable<CompletableFuture<HttpResponse<String>>> fn, Class<T> responseType)
            throws ServerFacadeException {
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
            throw new RequestErrorException(e);
        }

        return responseFuture;
    }


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
