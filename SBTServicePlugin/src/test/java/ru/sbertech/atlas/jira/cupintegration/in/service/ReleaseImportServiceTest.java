package ru.sbertech.atlas.jira.cupintegration.in.service;

import com.atlassian.jira.bc.project.DefaultProjectService;
import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.bc.project.version.DefaultVersionService;
import com.atlassian.jira.bc.project.version.VersionBuilder;
import com.atlassian.jira.bc.project.version.VersionService;
import com.atlassian.jira.mock.project.MockVersion;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.UserUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.in.ParamsEnricher;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;
import ru.sbertech.atlas.jira.cupintegration.in.release.ReleaseCreateStrategy;
import ru.sbertech.atlas.jira.cupintegration.in.release.ReleaseUpdateStrategy;
import ru.sbertech.atlas.jira.cupintegration.in.validator.ImportReleaseValidator;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Dmitriev Vladimir
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ReleaseImportService.class, UserUtils.class})
public class ReleaseImportServiceTest {

    private ProjectService mockProjectService;
    private VersionService mockVersionService;
    private ImportSettingsServiceJiraStorage mockImportSettingsServiceJiraStorage;
    private ReleaseMappingServiceJiraStorage mockReleaseMappingServiceJiraStorage;
    private ParamsEnricher mockParamsEnricher;
    private ImportReleaseValidator importReleaseValidator;

    private ReleaseImportService releaseImportService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        mockProjectService = mock(DefaultProjectService.class);
        mockVersionService = mock(DefaultVersionService.class);
        mockImportSettingsServiceJiraStorage = mock(ImportSettingsServiceJiraStorage.class);
        mockReleaseMappingServiceJiraStorage = mock(ReleaseMappingServiceJiraStorage.class);
        mockParamsEnricher = mock(ParamsEnricher.class);
        importReleaseValidator = mock(ImportReleaseValidator.class);

        releaseImportService = new ReleaseImportService(mockProjectService, mockVersionService, mockParamsEnricher, mockImportSettingsServiceJiraStorage,
            mockReleaseMappingServiceJiraStorage, importReleaseValidator);
    }

    @Test
    public void testSave_EmptyParamsMap_NullArgumentException() throws Exception {
        Map<String, String> params = new HashMap<>();

        ReleaseMapping mockReleaseMapping = mock(ReleaseMapping.class);
        when(mockReleaseMapping.getPpmReleaseAreaPs()).thenReturn("ppm_release_area_ps");
        when(mockReleaseMapping.getPpmReleaseId()).thenReturn("ppm_release_id");
        when(mockReleaseMapping.getPpmReleaseName()).thenReturn("ppm_release_name");
        when(mockReleaseMapping.getPpmReleaseStartDate()).thenReturn("ppm_release_start_date");
        when(mockReleaseMapping.getPpmReleaseFinishDate()).thenReturn("ppm_release_finish_date");
        when(mockReleaseMapping.getPpmReleaseStatus()).thenReturn("ppm_release_status");

        when(mockReleaseMappingServiceJiraStorage.getReleaseMapping()).thenReturn(mockReleaseMapping);

        ImportResult importResult = releaseImportService.importObject(params);

        assertEquals(ResultState.ERROR, importResult.getState());
        assertEquals("Input params must not be null.", importResult.getValue());
    }

    @Test
    public void testSave_EmptyReleaseId_NullArgumentException() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("ppm_release_id", "");
        params.put("ppm_release_name", "DAY PSI2");
        params.put("ppm_release_area_ps", "SCRUM");

        ReleaseMapping mockReleaseMapping = mock(ReleaseMapping.class);
        when(mockReleaseMapping.getPpmReleaseAreaPs()).thenReturn("ppm_release_area_ps");
        when(mockReleaseMapping.getPpmReleaseId()).thenReturn("ppm_release_id");
        when(mockReleaseMapping.getPpmReleaseName()).thenReturn("ppm_release_name");
        when(mockReleaseMapping.getPpmReleaseStartDate()).thenReturn("ppm_release_start_date");
        when(mockReleaseMapping.getPpmReleaseFinishDate()).thenReturn("ppm_release_finish_date");
        when(mockReleaseMapping.getPpmReleaseStatus()).thenReturn("ppm_release_status");

        when(mockReleaseMappingServiceJiraStorage.getReleaseMapping()).thenReturn(mockReleaseMapping);

        ImportResult importResult = releaseImportService.importObject(params);

        assertEquals(ResultState.ERROR, importResult.getState());
        assertEquals("The required tag \"ppm_release_id\" must not be null.", importResult.getValue());
    }

    @Test
    public void testSave_EmptyAreaPS_NullArgumentException() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("ppm_release_id", "44656457");
        params.put("ppm_release_name", "DAY PSI2");
        params.put("ppm_release_area_ps", "");

        ReleaseMapping mockReleaseMapping = mock(ReleaseMapping.class);
        when(mockReleaseMapping.getPpmReleaseAreaPs()).thenReturn("ppm_release_area_ps");
        when(mockReleaseMapping.getPpmReleaseId()).thenReturn("ppm_release_id");
        when(mockReleaseMapping.getPpmReleaseName()).thenReturn("ppm_release_name");
        when(mockReleaseMapping.getPpmReleaseStartDate()).thenReturn("ppm_release_start_date");
        when(mockReleaseMapping.getPpmReleaseFinishDate()).thenReturn("ppm_release_finish_date");
        when(mockReleaseMapping.getPpmReleaseStatus()).thenReturn("ppm_release_status");

        when(mockReleaseMappingServiceJiraStorage.getReleaseMapping()).thenReturn(mockReleaseMapping);

        ImportResult importResult = releaseImportService.importObject(params);

        assertEquals(ResultState.ERROR, importResult.getState());
        assertEquals("The required tag \"ppm_release_area_ps\" must not be null.", importResult.getValue());
    }

    @Test
    public void testSave_CorrectedParamsMap_NewRelease() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("ppm_release_id", "44656456");
        params.put("ppm_release_name", "DAY PSI2");
        params.put("ppm_release_area_ps", "SCRUM");
        params.put("ppm_release_start_date", "30.01.2016");
        params.put("ppm_release_finish_date", "30.02.2016");

        ImportSettings importSettings = mock(ImportSettings.class);
        when(mockImportSettingsServiceJiraStorage.getImportSetting()).thenReturn(importSettings);
        when(importReleaseValidator.validateImportSettings(importSettings)).thenReturn(importSettings);
        ApplicationUser user = new MockApplicationUser("user");
        Project project = new MockProject(1L, params.get("ppm_release_area_ps"), params.get("ppm_release_area_ps"));
        VersionBuilder releaseBuilder = mock(VersionBuilder.class);

        ReleaseMapping mockReleaseMapping = mock(ReleaseMapping.class);
        when(mockReleaseMapping.getPpmReleaseAreaPs()).thenReturn("ppm_release_area_ps");
        when(mockReleaseMapping.getPpmReleaseId()).thenReturn("ppm_release_id");
        when(mockReleaseMapping.getPpmReleaseName()).thenReturn("ppm_release_name");
        when(mockReleaseMapping.getPpmReleaseStartDate()).thenReturn("ppm_release_start_date");
        when(mockReleaseMapping.getPpmReleaseFinishDate()).thenReturn("ppm_release_finish_date");
        when(mockReleaseMapping.getPpmReleaseStatus()).thenReturn("ppm_release_status");

        when(mockReleaseMappingServiceJiraStorage.getReleaseMapping()).thenReturn(mockReleaseMapping);

        PowerMockito.mockStatic(UserUtils.class);
        when(UserUtils.getUser(importSettings.userName)).thenReturn(user);
        when(importReleaseValidator.validateUser(user, importSettings.userName)).thenReturn(user);

        ProjectService.GetProjectResult projectResult = mock(ProjectService.GetProjectResult.class);
        when(mockProjectService.getProjectByKey(user, "SCRUM")).thenReturn(projectResult);
        when(importReleaseValidator.validateProject(projectResult)).thenReturn(project);

        VersionService.VersionsResult versions = mock(VersionService.VersionsResult.class);
        when(mockVersionService.getVersionsByProject(user, project)).thenReturn(versions);
        when(importReleaseValidator.validateReleases(versions, "44656456", "SCRUM")).thenReturn(new ArrayList<Version>());

        when(mockVersionService.newVersionBuilder()).thenReturn(releaseBuilder);
        ReleaseCreateStrategy mockReleaseCreateStrategy = mock(ReleaseCreateStrategy.class);
        when(mockReleaseCreateStrategy.importRelease(user, releaseBuilder, null, project.getName()))
            .thenReturn(new ImportResult(ResultType.RELEASE, ResultState.CREATED, params.get("ppm_release_name")));
        PowerMockito.whenNew(ReleaseCreateStrategy.class).withArguments(any()).thenReturn(mockReleaseCreateStrategy);

        ImportResult importResult = releaseImportService.importObject(params);

        assertEquals(ResultState.CREATED, importResult.getState());
        assertEquals(params.get("ppm_release_name"), importResult.getValue());
    }

    @Test
    public void testSave_CorrectedParamsMap_Exception() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("ppm_release_id", "44656456");
        params.put("ppm_release_name", "DAY PSI2");
        params.put("ppm_release_area_ps", "SCRUM");
        params.put("ppm_release_start_date", "30.01.2016");
        params.put("ppm_release_finish_date", "30.02.2016");

        ImportSettings importSettings = mock(ImportSettings.class);
        when(mockImportSettingsServiceJiraStorage.getImportSetting()).thenReturn(importSettings);
        when(importReleaseValidator.validateImportSettings(importSettings)).thenReturn(importSettings);
        ApplicationUser user = new MockApplicationUser("user");
        Project project = new MockProject(1L, params.get("ppm_release_area_ps"), params.get("ppm_release_area_ps"));
        VersionBuilder releaseBuilder = mock(VersionBuilder.class);

        ReleaseMapping mockReleaseMapping = mock(ReleaseMapping.class);
        when(mockReleaseMapping.getPpmReleaseAreaPs()).thenReturn("ppm_release_area_ps");
        when(mockReleaseMapping.getPpmReleaseId()).thenReturn("ppm_release_id");
        when(mockReleaseMapping.getPpmReleaseName()).thenReturn("ppm_release_name");
        when(mockReleaseMapping.getPpmReleaseStartDate()).thenReturn("ppm_release_start_date");
        when(mockReleaseMapping.getPpmReleaseFinishDate()).thenReturn("ppm_release_finish_date");
        when(mockReleaseMapping.getPpmReleaseStatus()).thenReturn("ppm_release_status");

        when(mockReleaseMappingServiceJiraStorage.getReleaseMapping()).thenReturn(mockReleaseMapping);


        PowerMockito.mockStatic(UserUtils.class);
        when(UserUtils.getUser(importSettings.userName)).thenReturn(user);
        when(importReleaseValidator.validateUser(user, importSettings.userName)).thenReturn(user);

        ProjectService.GetProjectResult projectResult = mock(ProjectService.GetProjectResult.class);
        when(mockProjectService.getProjectByKey(user, "SCRUM")).thenReturn(projectResult);
        when(importReleaseValidator.validateProject(projectResult)).thenReturn(project);

        VersionService.VersionsResult versions = mock(VersionService.VersionsResult.class);
        when(mockVersionService.getVersionsByProject(user, project)).thenReturn(versions);
        when(importReleaseValidator.validateReleases(versions, "44656456", "SCRUM")).thenReturn(new ArrayList<Version>());

        when(mockVersionService.newVersionBuilder()).thenReturn(releaseBuilder);
        ReleaseCreateStrategy mockReleaseCreateStrategy = mock(ReleaseCreateStrategy.class);
        when(mockReleaseCreateStrategy.importRelease(user, releaseBuilder, null, project.getName())).thenThrow(new RuntimeException("Some exception"));
        PowerMockito.whenNew(ReleaseCreateStrategy.class).withArguments(any()).thenReturn(mockReleaseCreateStrategy);

        ImportResult importResult = releaseImportService.importObject(params);

        assertEquals(ResultState.ERROR, importResult.getState());
        assertEquals("Some exception", importResult.getValue());
    }

    @Test
    public void testSave_CorrectedParamsMap_UpdatedRelease() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("ppm_release_id", "44656457");
        params.put("ppm_release_name", "DAY PSI3");
        params.put("ppm_release_area_ps", "SCRUM");
        params.put("ppm_release_start_date", "30.01.2016");
        params.put("ppm_release_finish_date", "30.02.2016");

        ApplicationUser user = new MockApplicationUser("user");
        Project project = new MockProject(1L, params.get("ppm_release_area_ps"), params.get("ppm_release_area_ps"));
        VersionBuilder releaseBuilder = mock(VersionBuilder.class);
        when(mockVersionService.newVersionBuilder(any(Version.class))).thenReturn(releaseBuilder);
        List<Version> releases = new ArrayList<>();
        releases.add(new MockVersion(1L, params.get("ppm_release_id") + "_DAY PSI2"));

        ReleaseMapping mockReleaseMapping = mock(ReleaseMapping.class);
        when(mockReleaseMapping.getPpmReleaseAreaPs()).thenReturn("ppm_release_area_ps");
        when(mockReleaseMapping.getPpmReleaseId()).thenReturn("ppm_release_id");
        when(mockReleaseMapping.getPpmReleaseName()).thenReturn("ppm_release_name");
        when(mockReleaseMapping.getPpmReleaseStartDate()).thenReturn("ppm_release_start_date");
        when(mockReleaseMapping.getPpmReleaseFinishDate()).thenReturn("ppm_release_finish_date");
        when(mockReleaseMapping.getPpmReleaseStatus()).thenReturn("ppm_release_status");

        when(mockReleaseMappingServiceJiraStorage.getReleaseMapping()).thenReturn(mockReleaseMapping);
        ImportSettings importSettings = mock(ImportSettings.class);
        when(mockImportSettingsServiceJiraStorage.getImportSetting()).thenReturn(importSettings);
        when(importReleaseValidator.validateImportSettings(importSettings)).thenReturn(importSettings);

        PowerMockito.mockStatic(UserUtils.class);
        when(UserUtils.getUser(importSettings.userName)).thenReturn(user);
        when(importReleaseValidator.validateUser(user, importSettings.userName)).thenReturn(user);

        ProjectService.GetProjectResult projectResult = mock(ProjectService.GetProjectResult.class);
        when(mockProjectService.getProjectByKey(user, "SCRUM")).thenReturn(projectResult);
        when(importReleaseValidator.validateProject(projectResult)).thenReturn(project);

        VersionService.VersionsResult versions = mock(VersionService.VersionsResult.class);
        when(mockVersionService.getVersionsByProject(user, project)).thenReturn(versions);
        when(importReleaseValidator.validateReleases(versions, "44656457", "SCRUM")).thenReturn(releases);

        when(mockVersionService.newVersionBuilder()).thenReturn(releaseBuilder);
        ReleaseUpdateStrategy mockReleaseUpdateStrategy = mock(ReleaseUpdateStrategy.class);
        when(mockReleaseUpdateStrategy.importRelease(user, releaseBuilder, null, project.getName()))
            .thenReturn(new ImportResult(ResultType.RELEASE, ResultState.UPDATED, params.get("ppm_release_name")));
        PowerMockito.whenNew(ReleaseUpdateStrategy.class).withArguments(any()).thenReturn(mockReleaseUpdateStrategy);

        ImportResult importResult = releaseImportService.importObject(params);

        assertEquals(ResultState.UPDATED, importResult.getState());
        assertEquals(params.get("ppm_release_name"), importResult.getValue());
    }
}
