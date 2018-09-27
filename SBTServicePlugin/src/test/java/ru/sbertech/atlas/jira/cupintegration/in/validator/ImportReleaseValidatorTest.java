package ru.sbertech.atlas.jira.cupintegration.in.validator;

import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.bc.project.version.VersionService;
import com.atlassian.jira.mock.project.MockVersion;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by SBT-Dmitriyev-VV on 01.09.2016.
 */
public class ImportReleaseValidatorTest {
    private ImportReleaseValidator importReleaseValidator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        importReleaseValidator = new ImportReleaseValidator();
    }

    @Test
    public void testValidateImportSettings_Null_ImportExeption() throws ImportException{
        thrown.expect(ImportException.class);
        thrown.expectMessage("Import settings must not be null.");

        importReleaseValidator.validateImportSettings(null);
    }

    @Test
    public void testValidateImportSettings_ImportSettings_ImportSettings() throws ImportException{
        ImportSettings importSettings = mock(ImportSettings.class);

        ImportSettings result = importReleaseValidator.validateImportSettings(importSettings);

        assertEquals(importSettings, result);
    }

    @Test
    public void testValidateUser_NullUser_ImportException() throws ImportException {
        thrown.expect(ImportException.class);
        thrown.expectMessage("User with name \"testUser\" not found.");

        importReleaseValidator.validateUser(null, "testUser");
    }

    @Test
    public void testValidateUser_ValidUser_ApplicationUser() throws ImportException {
        ApplicationUser user = new MockApplicationUser("testUser");

        ApplicationUser result = importReleaseValidator.validateUser(user, "testUser");

        assertEquals("testUser", result.getName());
    }

    @Test
    public void testValidateProject_InvalidProject_ImportException() throws ImportException {
        ProjectService.GetProjectResult projectResult = mock(ProjectService.GetProjectResult.class);
        when(projectResult.isValid()).thenReturn(false);
        ErrorCollection errorCollection =  mock(ErrorCollection.class);
        when(errorCollection.toString()).thenReturn("Invalid Project");
        when(projectResult.getErrorCollection()).thenReturn(errorCollection);

        thrown.expect(ImportException.class);
        thrown.expectMessage("Invalid Project");

        importReleaseValidator.validateProject(projectResult);
    }


    @Test
    public void testValidateProject_ValidProject_Project() throws ImportException {
        ProjectService.GetProjectResult projectResult = mock(ProjectService.GetProjectResult.class);
        when(projectResult.isValid()).thenReturn(true);
        Project projectObject = new MockProject(1L, "project");
        when(projectResult.getProject()).thenReturn(projectObject);

        Project result = importReleaseValidator.validateProject(projectResult);

        assertEquals(projectObject.getId(), result.getId());
        assertEquals(projectObject.getName(), result.getName());
    }

    @Test
    public void testValidateReleases_InvalidVersionResult_ImportException() throws ImportException {
        VersionService.VersionsResult versions = mock(VersionService.VersionsResult.class);
        when(versions.isValid()).thenReturn(false);
        ErrorCollection errorCollection =  mock(ErrorCollection.class);
        when(errorCollection.toString()).thenReturn("Invalid Versions");
        when(versions.getErrorCollection()).thenReturn(errorCollection);
        String ppmReleaseId = "1";
        String projectName = "test";

        thrown.expect(ImportException.class);
        thrown.expectMessage("Invalid Versions");

        importReleaseValidator.validateReleases(versions, ppmReleaseId, projectName);
    }


    @Test
    public void testValidateReleases_FewVersions_ImportException() throws ImportException {
        VersionService.VersionsResult versions = mock(VersionService.VersionsResult.class);
        when(versions.isValid()).thenReturn(true);
        ArrayList<Version> versionArrayList = new ArrayList<>();
        Version firstVersion = new MockVersion(1L, "1_release-1.0.0");
        versionArrayList.add(firstVersion);
        Version lastVersion = new MockVersion(2L, "1_release-1.0.1");
        versionArrayList.add(lastVersion);
        when(versions.getVersions()).thenReturn(versionArrayList);
        String ppmReleaseId = "1";
        String projectName = "test";

        thrown.expect(ImportException.class);
        thrown.expectMessage("More then one releases with ppm_release_id \"1\" in project \"test\"");

        importReleaseValidator.validateReleases(versions, ppmReleaseId, projectName);
    }

    @Test
    public void testValidateReleases_OneVersion_Version() throws ImportException {
        VersionService.VersionsResult versions = mock(VersionService.VersionsResult.class);
        when(versions.isValid()).thenReturn(true);
        ArrayList<Version> versionArrayList = new ArrayList<>();
        Version firstVersion = new MockVersion(1L, "1_release-1.0.0");
        versionArrayList.add(firstVersion);
        when(versions.getVersions()).thenReturn(versionArrayList);
        String ppmReleaseId = "1";
        String projectName = "test";

        List<Version> result = importReleaseValidator.validateReleases(versions, ppmReleaseId, projectName);

        assertEquals(firstVersion.getId(), result.get(0).getId());
        assertEquals(firstVersion.getName(), result.get(0).getName());
    }
}
