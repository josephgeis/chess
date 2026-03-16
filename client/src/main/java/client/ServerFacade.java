package client;

import request.*;
import response.*;

import java.util.Collections;

public class ServerFacade {
    String host;
    int port;

    ChessHttpClient httpClient;

    public ServerFacade(String host, int port) {
        this.host = host;
        this.port = port;
        this.httpClient = new ChessHttpClient(host, port);
    }

    public RegisterResponse registerUser(RegisterRequest request) {
        return new RegisterResponse("joseph", "fakeToken");
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
}
