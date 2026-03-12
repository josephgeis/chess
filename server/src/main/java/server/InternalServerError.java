package server;

import java.util.Arrays;

public class InternalServerError extends RequestException {
    InternalServerError(Exception cause) {
        super(500, "Internal Server Error", cause);
    }

    class ErrorResponse extends RequestException.ErrorResponse {
        String stackTrace;
        ErrorResponse() {
            super();
            stackTrace = Arrays.toString(getStackTrace());
        }
    }

    @Override
    RequestException.ErrorResponse asObject() {
        return new ErrorResponse();
    }
}
