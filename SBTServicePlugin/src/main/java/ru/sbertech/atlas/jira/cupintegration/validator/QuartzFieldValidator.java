package ru.sbertech.atlas.jira.cupintegration.validator;

import com.atlassian.core.cron.parser.CronExpressionParser;
import ru.sbertech.atlas.jira.cupintegration.exception.ValidateException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;

public class QuartzFieldValidator extends FieldValidator {
    @Override
    public void validate(Object owner, String fieldName, Object fieldValue) throws ValidateException {
        if (!(owner instanceof ImportSettings)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " works only with " + ImportSettings.class.getSimpleName());
        }
        if (!((ImportSettings) owner).autoImportEnabled) {
            return;
        }
        if (fieldValue == null) {
            throw new ValidateException(fieldName, "Auto Import is enabled, scheduler required!");
        }
        try {
            new CronExpressionParser(fieldValue.toString());
        } catch (IllegalArgumentException e) {
            throw new ValidateException(fieldName, e.getMessage());
        }
    }
}
