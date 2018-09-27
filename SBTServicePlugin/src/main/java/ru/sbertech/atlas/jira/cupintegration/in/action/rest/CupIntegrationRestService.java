package ru.sbertech.atlas.jira.cupintegration.in.action.rest;

import ru.sbertech.atlas.jira.cupintegration.exception.ValidateException;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportSettingsUpdateException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.in.service.ImportSettingService;
import ru.sbertech.atlas.jira.cupintegration.in.service.ReleaseMappingService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/")
public class CupIntegrationRestService {
    private final ImportSettingService importSettingService;
    private final ReleaseMappingService releaseMappingService;

    public CupIntegrationRestService(ImportSettingService importSettingService, ReleaseMappingService releaseMappingService) {
        this.importSettingService = importSettingService;
        this.releaseMappingService = releaseMappingService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/updateImportSettings")
    public ValidateException[] updateImportSettings(ImportSettings importSettings) throws IOException {
        try {
            importSettingService.updateOrCreateImportSetting(importSettings);
            return null;
        } catch (ImportSettingsUpdateException e) {
            return e.exceptions;
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/updateReleaseMapping")
    public ValidateException[] updateReleaseMapping(ReleaseMapping releaseMapping) throws IOException {
        try {
            releaseMappingService.createOrUpdateReleaseMapping(releaseMapping);
            return null;
        } catch (ImportSettingsUpdateException e) {
            return e.exceptions;
        }
    }
}
