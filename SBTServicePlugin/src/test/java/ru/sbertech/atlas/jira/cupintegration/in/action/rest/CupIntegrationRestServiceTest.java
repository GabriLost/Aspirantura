package ru.sbertech.atlas.jira.cupintegration.in.action.rest;

import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.exception.ValidateException;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportSettingsUpdateException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.in.service.ImportSettingService;
import ru.sbertech.atlas.jira.cupintegration.in.service.ReleaseMappingService;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class CupIntegrationRestServiceTest {

    @Test
    public void testUpdateImportSettings_ImportSettings_OkResponse() throws Exception {
        ImportSettingService importSettingService = mock(ImportSettingService.class);
        ImportSettings importSettings = new ImportSettings("/", "* * * * * ?", false, "cupZniId", "userName", null);

        CupIntegrationRestService restService = new CupIntegrationRestService(importSettingService, null);
        ValidateException[] result = restService.updateImportSettings(importSettings);

        assertNull(result);
        verify(importSettingService, times(1)).updateOrCreateImportSetting(importSettings);
    }

    @Test
    public void testUpdateImportSettings_IncorrectData_ValidateExceptions() throws Exception {
        ImportSettingService importSettingService = mock(ImportSettingService.class);
        ImportSettings importSettings = new ImportSettings("/", "* * * * * ?", false, "cupZniId", "userName", null);
        doThrow(new ImportSettingsUpdateException(Collections.singletonList(new ValidateException("test", "test")))).when(importSettingService).updateOrCreateImportSetting(importSettings);

        CupIntegrationRestService restService = new CupIntegrationRestService(importSettingService, null);
        ValidateException[] result = restService.updateImportSettings(importSettings);

        assertNotNull(result);
        assertEquals(1, result.length);
        verify(importSettingService, times(1)).updateOrCreateImportSetting(importSettings);
    }

    @Test
    public void testUpdateReleaseMapping_ReleaseMapping_OkResponse() throws Exception {
        ReleaseMappingService releaseMappingService = mock(ReleaseMappingService.class);
        ReleaseMapping releaseMapping = new ReleaseMapping("ppm_release_id", "ppm_release_name", "ppm_release_area_ps", "ppm_release_start_date", "ppm_release_finish_date",
            "ppm_release_status");

        CupIntegrationRestService restService = new CupIntegrationRestService(null, releaseMappingService);
        ValidateException[] result = restService.updateReleaseMapping(releaseMapping);

        assertNull(result);
        verify(releaseMappingService, times(1)).createOrUpdateReleaseMapping(releaseMapping);
    }

    @Test
    public void testUpdateReleaseMapping_ReleaseMapping_ValidateExceptions() throws Exception {
        ReleaseMappingService releaseMappingService = mock(ReleaseMappingService.class);
        ReleaseMapping releaseMapping = new ReleaseMapping("ppm_release_id", "ppm_release_name", "ppm_release_area_ps", "ppm_release_start_date", "ppm_release_finish_date",
            "ppm_release_status");
        doThrow(new ImportSettingsUpdateException(Collections.singletonList(new ValidateException("test", "test")))).when(releaseMappingService).createOrUpdateReleaseMapping(releaseMapping);

        CupIntegrationRestService restService = new CupIntegrationRestService(null, releaseMappingService);
        ValidateException[] result = restService.updateReleaseMapping(releaseMapping);

        assertNotNull(result);
        assertEquals(1, result.length);
        verify(releaseMappingService, times(1)).createOrUpdateReleaseMapping(releaseMapping);
    }
}
