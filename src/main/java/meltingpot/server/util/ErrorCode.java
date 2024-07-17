package meltingpot.server.util;

public enum ErrorCode {
    INVALID_MESSAGE(400, "Invalid message format."),
    INVALID_TOKEN(401, "Invalid token.");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
