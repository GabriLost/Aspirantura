package ru.sbertech.atlas.jira.cupintegration.validator;

import ru.sbertech.atlas.jira.cupintegration.exception.ValidateException;

public abstract class FieldValidator {

    /**
     * It must be to reflection API works fine
     */
    public FieldValidator() {

    }

    public abstract void validate(Object owner, String field, Object objectToValidate) throws ValidateException;
}
