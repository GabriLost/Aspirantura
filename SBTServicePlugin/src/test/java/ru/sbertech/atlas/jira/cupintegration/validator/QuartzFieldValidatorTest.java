package ru.sbertech.atlas.jira.cupintegration.validator;

import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.exception.ValidateException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;

import static org.junit.Assert.assertEquals;


public class QuartzFieldValidatorTest {

    @Test
    public void testValidate_OwnerInteger_IllegalArgumentException() throws Exception {
        QuartzFieldValidator quartzFieldValidator = new QuartzFieldValidator();

        try {
            quartzFieldValidator.validate(1, null, null);
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            assertEquals("QuartzFieldValidator works only with ImportSettings", e.getMessage());
        }
    }

    @Test
    public void testValidate_AutoImportEnabledFieldValueNull_ValidateException() throws Exception {
        ImportSettings importSettings = new ImportSettings(null, null, true, null, null, null);

        QuartzFieldValidator quartzFieldValidator = new QuartzFieldValidator();
        //TODO: rewrite EXPECTED EXCEPTION on THROWN
        try {
            quartzFieldValidator.validate(importSettings, "quartzExpression", null);
        } catch (Exception e) {
            assertEquals(ValidateException.class, e.getClass());
            assertEquals("Auto Import is enabled, scheduler required!", ((ValidateException) e).message);
            assertEquals("quartzExpression", ((ValidateException) e).field);
        }
    }

    @Test
    public void testValidateAutoImportEnabledFieldValueIncorrect_ValidateException() throws Exception {
        ImportSettings importSettings = new ImportSettings(null, null, true, null, null, null);

        QuartzFieldValidator quartzFieldValidator = new QuartzFieldValidator();
        //TODO: rewrite EXPECTED EXCEPTION on THROWN
        try {
            quartzFieldValidator.validate(importSettings, "quartzExpression", "incorrect");
        } catch (Exception e) {
            assertEquals(ValidateException.class, e.getClass());
            assertEquals("The provided cron string does not have 7 parts: incorrect", ((ValidateException) e).message);
            assertEquals("quartzExpression", ((ValidateException) e).field);
        }
    }
}
