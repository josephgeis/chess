package response;

import model.AuthData;

public record RegisterResponse(String username, String authToken) {
    public static RegisterResponse from(AuthData authData) {
        return new RegisterResponse(authData.username(), authData.authToken());
    }
}
