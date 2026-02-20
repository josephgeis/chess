 package server;

import com.google.gson.Gson;

/// Base exception class for request errors
public abstract class InvalidRequestException extends Exception {
    final int statusCode;

    record ErrorResponse(String message) {}

    protected InvalidRequestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String responseAsJson(Gson gson) {
        return gson.toJson(new ErrorResponse("Error: " + getMessage()));
    }

    public String responseAsJson() {
        return responseAsJson(new Gson());
    }
}
