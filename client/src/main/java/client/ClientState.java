package client;

import model.AuthData;

public final class ClientState {
    boolean quit = false;

    public void quitProgram() {
        quit = true;
    }

    AuthData authData = null;

    public String getAuthToken() {
        return authData.authToken();
    }

    public String getLoggedInUser() {
        return authData.username();
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }
}
