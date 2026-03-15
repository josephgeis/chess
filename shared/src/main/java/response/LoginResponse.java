package response;

import model.AuthData;

public record LoginResponse(String username, String authToken) {
    public static LoginResponse from(AuthData authData) {
        return new LoginResponse(authData.username(), authData.authToken());
    }
}
