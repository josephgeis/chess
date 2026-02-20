 package handler;

import com.google.gson.Gson;

/// Base exception class for request errors
public abstract class InvalidRequestException extends Exception {
    final int statusCode;

    static final Gson gson = new Gson();

    record ErrorResponse(String message) {}

    protected InvalidRequestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String responseAsJson() {
        return gson.toJson(new ErrorResponse("Error: " + getMessage()));
    }
}
