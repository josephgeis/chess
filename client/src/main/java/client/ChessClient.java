package client;

import model.AuthData;
import request.LoginRequest;
import response.LoginResponse;

import java.util.concurrent.CompletableFuture;

public class ChessClient {
    private final ServerFacade serverFacade;
    private final ClientState clientState;

    public ChessClient(ServerFacade serverFacade, ClientState clientState) {
        this.serverFacade = serverFacade;
        this.clientState = clientState;
    }

    public CompletableFuture<LoginResponse> makeLoginRequest(String username, String password) {

        LoginRequest request = new LoginRequest(username, password);
        try {
            return serverFacade.loginUserAsync(request).thenApply(loginResponse -> {
                        clientState.setAuthData(
                                new AuthData(loginResponse.authToken(),
                                        loginResponse.username())
                        );
                        return loginResponse;
                    })
                    .exceptionallyCompose(throwable -> {
                        if (throwable instanceof ServerFacade.ErrorResponseException) {
                            return CompletableFuture.failedFuture(throwable);
                        } else {
                            return CompletableFuture.failedFuture(new Throwable("Unexpected error"));
                        }
                    });
        } catch (ServerFacade.ServerFacadeException e) {
            return CompletableFuture.failedFuture(e.getCause());
        }
    }
}
