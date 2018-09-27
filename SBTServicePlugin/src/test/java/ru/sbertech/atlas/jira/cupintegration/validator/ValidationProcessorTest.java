package ru.sbertech.atlas.jira.cupintegration.validator;

import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.annotation.Validator;
import ru.sbertech.atlas.jira.cupintegration.exception.ValidateException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ValidationProcessorTest {

    public static FieldValidator monitor;

    @Test
    public void testValidateSetting() throws Exception {
        monitor = null;
        MockImportSettings importSettings = new MockImportSettings();
        monitor = mock(FieldValidator.class);

        ValidationProcessor validationProcessor = new ValidationProcessor();
        validationProcessor.validateSetting(importSettings);

        verify(monitor, times(1)).validate(eq(importSettings), eq("importFolder"), eq("/"));
    }


    public static class MockValidator extends FieldValidator {
        public MockValidator() {

        }

        @Override
        public void validate(Object owner, String field, Object objectToValidate) throws ValidateException {
            monitor.validate(owner, field, objectToValidate);
        }
    }


    private class MockImportSettings extends ImportSettings {
        @Validator(clazz = MockValidator.class)
        public String importFolder = "/";
    }
}