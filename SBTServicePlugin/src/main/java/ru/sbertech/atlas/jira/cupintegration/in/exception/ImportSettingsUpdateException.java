package ru.sbertech.atlas.jira.cupintegration.in.exception;

import ru.sbertech.atlas.jira.cupintegration.exception.ValidateException;

import java.util.List;

public class ImportSettingsUpdateException extends Exception {
    public ValidateException[] exceptions;

    public ImportSettingsUpdateException(List<ValidateException> validateExceptions) {
        this.exceptions = validateExceptions.toArray(new ValidateException[validateExceptions.size() - 1]);
    }
}
