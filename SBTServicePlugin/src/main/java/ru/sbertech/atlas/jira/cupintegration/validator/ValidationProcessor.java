package ru.sbertech.atlas.jira.cupintegration.validator;


import ru.sbertech.atlas.jira.cupintegration.annotation.Validator;
import ru.sbertech.atlas.jira.cupintegration.exception.ValidateException;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportSettingsUpdateException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ValidationProcessor<T> {

    public void validateSetting(T settings) throws ImportSettingsUpdateException {
        Field[] fields = settings.getClass().getFields();
        List<ValidateException> validateExceptions = new ArrayList<>();
        for (Field f : fields) {
            Validator annotation = f.getAnnotation(Validator.class);
            if (annotation == null) {
                continue;
            }
            try {
                FieldValidator validator = (FieldValidator) annotation.clazz().newInstance();
                validator.validate(settings, f.getName(), f.get(settings));
            } catch (IllegalAccessException | InstantiationException e) {
                // it will NEVER happens
                throw new RuntimeException(e);
            } catch (ValidateException e) {
                validateExceptions.add(e);
            }
        }
        if (validateExceptions.size() > 0) {
            throw new ImportSettingsUpdateException(validateExceptions);
        }
    }
}
