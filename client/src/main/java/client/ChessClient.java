package client;

import model.AuthData;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class ChessClient {
    private final ServerFacade serverFacade;
    private final ClientState clientState;

    public ChessClient(ServerFacade serverFacade, ClientState clientState) {
        this.serverFacade = serverFacade;
        this.clientState = clientState;
    }

    public ClientState getState() {
        return clientState;
    }

    CompletableFuture<Void> makeRequest(Supplier<CompletableFuture<Void>> request) {
        return makeRequest(request, Void.TYPE);
    }

    <T> CompletableFuture<T> makeRequest(Supplier<CompletableFuture<T>> request, Class<T> ignored) {
        return request.get().exceptionallyCompose(throwable -> {
            if (throwable instanceof ServerFacade.ErrorResponseException) {
                return CompletableFuture.failedFuture(throwable);
            } else {
                return CompletableFuture.failedFuture(new Throwable("Unexpected error"));
            }
        });
    }

    public CompletableFuture<LoginResponse> makeLoginRequest(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
        return makeRequest(() -> serverFacade.loginUserAsync(request), LoginResponse.class)
                .thenApply(loginResponse -> {
                    clientState.setAuthData(
                            new AuthData(loginResponse.authToken(),
                                    loginResponse.username())
                    );
                    return loginResponse;
                });
    }

    public CompletableFuture<Void> makeLogoutRequest() {
        return makeRequest(() -> serverFacade.logoutUserAsync(clientState.getAuthToken()))
                .thenApply(unused -> {
                    clientState.setAuthData(null);
                    return unused;
                });
    }

    public CompletableFuture<RegisterResponse> makeRegisterRequest(String username, String password, String email) {
        RegisterRequest request = new RegisterRequest(username, password, email);
        return makeRequest(() -> serverFacade.registerUserAsync(request), RegisterResponse.class)
                .thenApply(response -> {
                    clientState.setAuthData(
                            new AuthData(response.authToken(),
                                    response.username())
                    );
                    return response;
                });
    }
}
