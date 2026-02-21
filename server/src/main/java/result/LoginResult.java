package result;

import model.AuthData;

public record LoginResult(String username, String authToken) {
    public static LoginResult from(AuthData authData) {
        return new LoginResult(authData.username(), authData.authToken());
    }
}
