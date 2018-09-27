package ru.sbertech.atlas.jira.cupintegration.exception;

public class ValidateException extends Exception {
    public String field;
    public String message;

    public ValidateException(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
