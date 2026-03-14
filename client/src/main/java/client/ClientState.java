package client;

public final class ClientState {
    static String authToken = null;

    public static String getAuthToken() {
        return authToken;
    }

    static void setAuthToken(String authToken) {
        ClientState.authToken = authToken;
    }

    public static boolean isLoggedIn() {
        return authToken != null;
    }
}
