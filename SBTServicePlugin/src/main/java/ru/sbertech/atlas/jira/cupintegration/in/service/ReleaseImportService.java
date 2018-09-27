package ru.sbertech.atlas.jira.cupintegration.in.service;

import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.bc.project.version.VersionBuilder;
import com.atlassian.jira.bc.project.version.VersionService;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import ru.sbertech.atlas.jira.cupintegration.in.ParamsEnricher;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;
import ru.sbertech.atlas.jira.cupintegration.in.release.AbstractReleaseImportStrategy;
import ru.sbertech.atlas.jira.cupintegration.in.release.ReleaseCreateStrategy;
import ru.sbertech.atlas.jira.cupintegration.in.release.ReleaseUpdateStrategy;
import ru.sbertech.atlas.jira.cupintegration.in.validator.ImportReleaseValidator;

import java.util.List;
import java.util.Map;

/**
 * @author Dmitriev Vladimir
 */
public class ReleaseImportService implements IImportProcessor {

    public static final String RELEASE = "Релиз";
    private static final Logger LOG = Logger.getLogger(ReleaseImportService.class);
    private final ProjectService projectService;
    private final VersionService versionService;
    private final ParamsEnricher paramsEnricher;
    private final ImportSettingsServiceJiraStorage importSettingsServiceJiraStorage;
    private final ReleaseMappingServiceJiraStorage releaseMappingServiceJiraStorage;
    private final ImportReleaseValidator importReleaseValidator;

    public ReleaseImportService(ProjectService projectService, VersionService versionService, ParamsEnricher paramsEnricher,
        ImportSettingsServiceJiraStorage importSettingsServiceJiraStorage, ReleaseMappingServiceJiraStorage releaseMappingServiceJiraStorage,
        ImportReleaseValidator importReleaseValidator) {
        this.projectService = projectService;
        this.versionService = versionService;
        this.paramsEnricher = paramsEnricher;
        this.importSettingsServiceJiraStorage = importSettingsServiceJiraStorage;
        this.releaseMappingServiceJiraStorage = releaseMappingServiceJiraStorage;
        this.importReleaseValidator = importReleaseValidator;
    }

    /**
     * @param params Map with tags and values from xml from PPM
     * @return Created/Updated release or String with errors
     */
    public ImportResult importObject(Map<String, String> params) {
        try {
            ReleaseMapping releaseMapping = releaseMappingServiceJiraStorage.getReleaseMapping();
            validateInputParams(releaseMapping, params);
            ImportSettings importSettings = importReleaseValidator.validateImportSettings(importSettingsServiceJiraStorage.getImportSetting());
            ApplicationUser user = importReleaseValidator.validateUser(UserUtils.getUser(importSettings.userName), importSettings.userName);
            Project project = importReleaseValidator.validateProject(projectService.getProjectByKey(user, params.get(releaseMapping.getPpmReleaseAreaPs())));
            List<Version> releases = importReleaseValidator.validateReleases(versionService.getVersionsByProject(user, project),
                params.get(releaseMapping.getPpmReleaseId()), project.getName());

            AbstractReleaseImportStrategy releaseImportStrategy;
            VersionBuilder releaseBuilder;
            if (releases.size() == 0) {
                releaseBuilder = versionService.newVersionBuilder();
                releaseImportStrategy = new ReleaseCreateStrategy(versionService);
            } else {
                releaseBuilder = versionService.newVersionBuilder(releases.get(0));
                releaseImportStrategy = new ReleaseUpdateStrategy(versionService);
            }
            paramsEnricher.enrichReleaseFields(project.getId(), releaseMapping, params, releaseBuilder);

            return releaseImportStrategy.importRelease(user, releaseBuilder, params.get(releaseMapping.getPpmReleaseStatus()), project.getKey());
        } catch (ImportException e) {
            return new ImportResult(ResultType.RELEASE, ResultState.ERROR, e.getMessage());
        } catch (Exception e) {
            LOG.error("ReleaseImportService couldn't procces." + e);
            return new ImportResult(ResultType.RELEASE, ResultState.ERROR, e.getMessage());
        }
    }

    private void validateInputParams(ReleaseMapping releaseMapping, Map<String, String> params) throws ImportException {
        if (MapUtils.isEmpty(params)) {
            throw new ImportException("Input params must not be null.");
        }
        if (StringUtils.isEmpty(params.get(releaseMapping.getPpmReleaseId())) || StringUtils.isEmpty(params.get(releaseMapping.getPpmReleaseAreaPs()))) {
            throw new ImportException("The required tag \"" + (StringUtils.isEmpty(params.get(releaseMapping.getPpmReleaseId())) ? releaseMapping.getPpmReleaseId()
                : releaseMapping.getPpmReleaseAreaPs()) + "\" must not be null.");
        }
    }
}
