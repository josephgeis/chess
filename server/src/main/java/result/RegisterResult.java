package result;

import model.AuthData;

public record RegisterResult(String username, String authToken) {
    public static RegisterResult from(AuthData authData) {
        return new RegisterResult(authData.username(), authData.authToken());
    }
}
