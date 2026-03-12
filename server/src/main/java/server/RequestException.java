 package server;

import com.google.gson.Gson;

/// Base exception class for request errors
public abstract class RequestException extends Exception {
    final int statusCode;

    class ErrorResponse {
        String message;

        ErrorResponse() {
            message = "Error: " + getMessage();
        }

        String toJson(Gson gson) {
            return gson.toJson(this);
        }
    }

    protected RequestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    protected RequestException(int statusCode, String message, Exception cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    ErrorResponse asObject() {
        return new ErrorResponse();
    }

    public String asJson(Gson gson) {
        return asObject().toJson(gson);
    }
}
