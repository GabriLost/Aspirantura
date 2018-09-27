package ru.sbertech.atlas.jira.cupintegration.in.action;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.in.service.ImportSettingsServiceJiraStorage;
import ru.sbertech.atlas.jira.cupintegration.in.service.ReleaseMappingServiceJiraStorage;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dmitriev Vladimir
 */
public class ConfigureImportActionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testConfigureImportAction_WithArguments() {
        ImportSettings expectedImportSetting = new ImportSettings("/", "* * * * * ?", false, "cupZniId", "userName", "cupKrpId");
        ReleaseMapping expectedReleaseMapping = new ReleaseMapping("ppm_release_id", "ppm_release_name", "ppm_release_area_ps", "ppm_release_start_date", "ppm_release_finish_date", "ppm_release_status");
        ImportSettingsServiceJiraStorage importSettingService = mock(ImportSettingsServiceJiraStorage.class);
        when(importSettingService.getImportSetting()).thenReturn(expectedImportSetting);
        ReleaseMappingServiceJiraStorage releaseMappingServiceJiraStorage = mock(ReleaseMappingServiceJiraStorage.class);
        when(releaseMappingServiceJiraStorage.getReleaseMapping()).thenReturn(expectedReleaseMapping);

        ConfigureImportAction configureImportAction = new ConfigureImportAction(importSettingService, releaseMappingServiceJiraStorage);

        assertEquals(expectedImportSetting, configureImportAction.getImportSettings());
        assertEquals(expectedReleaseMapping, configureImportAction.getReleaseMapping());
    }
}
