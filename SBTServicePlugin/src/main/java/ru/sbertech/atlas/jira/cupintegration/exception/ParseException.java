package ru.sbertech.atlas.jira.cupintegration.exception;

/**
 * Error while file parsing
 */
public class ParseException extends RuntimeException {

    public ParseException(String message, Exception cause) {
        super(message, cause);
    }
}
