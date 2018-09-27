package ru.sbertech.atlas.jira.cupintegration.in.service;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.validator.ValidationProcessor;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Dmitriev Vladimir
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ReleaseMappingServiceJiraStorage.class})
public class ReleaseMappingServiceJiraStorageTest {


    @Test
    public void testGetReleaseMapping() throws Exception {
        PluginSettingsFactory pluginSettingsFactory = mock(PluginSettingsFactory.class);
        PluginSettings pluginSettings = mock(PluginSettings.class);
        when(pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_ID)).thenReturn("ppm_release_id");
        when(pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_NAME)).thenReturn("ppm_release_name");
        when(pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_AREA_PS)).thenReturn("ppm_release_area_ps");
        when(pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_START_DATE)).thenReturn("ppm_release_start_date");
        when(pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_FINISH_DATE)).thenReturn("ppm_release_finish_date");
        when(pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_STATUS)).thenReturn("ppm_release_status");
        when(pluginSettingsFactory.createGlobalSettings()).thenReturn(pluginSettings);

        ReleaseMappingServiceJiraStorage jiraStorage = new ReleaseMappingServiceJiraStorage(pluginSettingsFactory);
        ReleaseMapping result = jiraStorage.getReleaseMapping();

        assertEquals("ppm_release_id", result.ppmReleaseId);
        assertEquals("ppm_release_name", result.ppmReleaseName);
        assertEquals("ppm_release_area_ps", result.ppmReleaseAreaPs);
        assertEquals("ppm_release_start_date", result.ppmReleaseStartDate);
        assertEquals("ppm_release_finish_date", result.ppmReleaseFinishDate);
        assertEquals("ppm_release_status", result.ppmReleaseStatus);
    }

    @Test
    public void testCreateOrUpdateReleaseMapping() throws Exception {
        PluginSettingsFactory pluginSettingsFactory = mock(PluginSettingsFactory.class);
        PluginSettings pluginSettings = new MockPluginSettings();
        when(pluginSettingsFactory.createGlobalSettings()).thenReturn(pluginSettings);
        ReleaseMapping releaseMapping = new ReleaseMapping("ppm_release_id", "ppm_release_name", "ppm_release_area_ps", "ppm_release_start_date", "ppm_release_finish_date",
            "ppm_release_status");
        @SuppressWarnings("unchecked")
        ValidationProcessor<ReleaseMapping> validationProcessor = mock(ValidationProcessor.class);
        PowerMockito.whenNew(ValidationProcessor.class).withNoArguments().thenReturn(validationProcessor);

        ReleaseMappingServiceJiraStorage jiraStorage = new ReleaseMappingServiceJiraStorage(pluginSettingsFactory);
        jiraStorage.createOrUpdateReleaseMapping(releaseMapping);

        assertEquals("ppm_release_id", pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_ID));
        assertEquals("ppm_release_name", pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_NAME));
        assertEquals("ppm_release_area_ps", pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_AREA_PS));
        assertEquals("ppm_release_start_date", pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_START_DATE));
        assertEquals("ppm_release_finish_date", pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_FINISH_DATE));
        assertEquals("ppm_release_status", pluginSettings.get(ReleaseMappingServiceJiraStorage.PPM_RELEASE_STATUS));
        verify(validationProcessor, times(1)).validateSetting(releaseMapping);
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
