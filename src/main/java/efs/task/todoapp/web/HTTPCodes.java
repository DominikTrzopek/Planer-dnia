package efs.task.todoapp.web;

public enum HTTPCodes {

    OK(200),
    CREATED (201),
    BAD_REQUEST(400),
    USER_MISSING(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    EXISTS(409);

    private final int value;

    HTTPCodes(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }


}
