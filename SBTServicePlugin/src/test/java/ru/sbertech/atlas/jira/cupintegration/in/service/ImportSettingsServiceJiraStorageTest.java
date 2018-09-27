package ru.sbertech.atlas.jira.cupintegration.in.service;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.validator.ValidationProcessor;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ImportSettingsServiceJiraStorage.class})
public class ImportSettingsServiceJiraStorageTest {

    @Test
    public void testGetImportSetting() throws Exception {
        PluginSettingsFactory pluginSettingsFactory = mock(PluginSettingsFactory.class);
        PluginSettings pluginSettings = mock(PluginSettings.class);
        when(pluginSettings.get(ImportSettingsServiceJiraStorage.IMPORT_SETTINGS_FOLDER)).thenReturn("/");
        when(pluginSettings.get(ImportSettingsServiceJiraStorage.IMPORT_SETTINGS_QUARTZ_EXPRESSION)).thenReturn("* * * * * * ?");
        when(pluginSettings.get(ImportSettingsServiceJiraStorage.IMPORT_SETTINGS_AUTO_EXPORT)).thenReturn("true");
        when(pluginSettings.get(ImportSettingsServiceJiraStorage.IMPORT_SETTINGS_CUP_ZNI_ID_FIELD)).thenReturn("cupZniId");
        when(pluginSettings.get(ImportSettingsServiceJiraStorage.IMPORT_SETTINGS_USER)).thenReturn("userName");
        when(pluginSettingsFactory.createGlobalSettings()).thenReturn(pluginSettings);

        ImportSettingsServiceJiraStorage jiraStorage = new ImportSettingsServiceJiraStorage(pluginSettingsFactory);
        ImportSettings result = jiraStorage.getImportSetting();

        assertEquals("/", result.importFolder);
        assertEquals("* * * * * * ?", result.quartzExpression);
        assertTrue(result.autoImportEnabled);
        assertEquals("cupZniId", result.cupZniIdField);
        assertEquals("userName", result.userName);
    }

    @Test
    public void testUpdateOrCreateImportSetting() throws Exception {
        PluginSettingsFactory pluginSettingsFactory = mock(PluginSettingsFactory.class);
        PluginSettings pluginSettings = new MockPluginSettings();
        when(pluginSettingsFactory.createGlobalSettings()).thenReturn(pluginSettings);
        ImportSettings importSettings = new ImportSettings("/", "* * * * * * ?", true, "cupZniId", "userName", null);
        @SuppressWarnings("unchecked")
        ValidationProcessor<ImportSettings> validationProcessor = mock(ValidationProcessor.class);
        PowerMockito.whenNew(ValidationProcessor.class).withNoArguments().thenReturn(validationProcessor);

        ImportSettingsServiceJiraStorage jiraStorage = new ImportSettingsServiceJiraStorage(pluginSettingsFactory);
        jiraStorage.updateOrCreateImportSetting(importSettings);

        assertEquals("/", pluginSettings.get(ImportSettingsServiceJiraStorage.IMPORT_SETTINGS_FOLDER));
        assertEquals("* * * * * * ?", pluginSettings.get(ImportSettingsServiceJiraStorage.IMPORT_SETTINGS_QUARTZ_EXPRESSION));
        assertTrue(Boolean.parseBoolean((String) pluginSettings.get(ImportSettingsServiceJiraStorage.IMPORT_SETTINGS_AUTO_EXPORT)));
        assertEquals("cupZniId", pluginSettings.get(ImportSettingsServiceJiraStorage.IMPORT_SETTINGS_CUP_ZNI_ID_FIELD));
        assertEquals("userName", pluginSettings.get(ImportSettingsServiceJiraStorage.IMPORT_SETTINGS_USER));
        verify(validationProcessor, times(1)).validateSetting(importSettings);
    }

    private class MockPluginSettings implements PluginSettings {
        private Map<String, Object> paramMap = new HashMap<>();

        @Override
        public Object get(String key) {
            return paramMap.get(key);
        }

        @Override
        public Object put(String key, Object value) {
            paramMap.put(key, value);
            return value;
        }

        @Override
        public Object remove(String key) {
            return null;
        }
    }
}
