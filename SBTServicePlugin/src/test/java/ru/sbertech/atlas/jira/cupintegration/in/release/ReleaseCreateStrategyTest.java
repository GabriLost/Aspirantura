package ru.sbertech.atlas.jira.cupintegration.in.release;

import com.atlassian.crowd.embedded.api.User;

import com.atlassian.jira.bc.ServiceOutcome;
import com.atlassian.jira.bc.project.version.DefaultVersionService;
import com.atlassian.jira.bc.project.version.VersionBuilder;
import com.atlassian.jira.bc.project.version.VersionService;
import com.atlassian.jira.mock.project.MockVersion;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.MockUser;
import com.atlassian.jira.util.ErrorCollection;
import org.junit.Before;
import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Dmitriev Vladimir
 */
public class ReleaseCreateStrategyTest {

    private ApplicationUser mockUser;
    private VersionBuilder mockReleaseBuilder;
    private VersionService mockVersionService;
    private ReleaseCreateStrategy releaseCreateStrategy;


    @Before
    public void setup() {
        mockUser = new MockApplicationUser("admin");
        mockReleaseBuilder = mock(VersionBuilder.class);
        mockVersionService = mock(DefaultVersionService.class);
        releaseCreateStrategy = new ReleaseCreateStrategy(mockVersionService);
    }

    @Test
    public void testImportRelease_invalidRelease_importErrorResult() throws Exception {
        ErrorCollection errorCollection = mock(ErrorCollection.class);
        when(errorCollection.toString()).thenReturn("validation error");
        VersionService.VersionBuilderValidationResult validationResult = mock(VersionService.VersionBuilderValidationResult.class);
        when(validationResult.isValid()).thenReturn(false);
        when(validationResult.getErrorCollection()).thenReturn(errorCollection);

        when(mockVersionService.validateCreate(mockUser, mockReleaseBuilder)).thenReturn(validationResult);

        ImportResult importResult = releaseCreateStrategy.importRelease(mockUser, mockReleaseBuilder, null, "project");

        assertEquals(ResultType.RELEASE, importResult.getType());
        assertEquals(ResultState.ERROR, importResult.getState());
        assertEquals("validation error", importResult.getValue());
    }

    @Test
    public void testImportRelease_validRelease_createErrorResult() throws Exception {
        ErrorCollection errorCollection = mock(ErrorCollection.class);
        when(errorCollection.toString()).thenReturn("create error");
        VersionService.VersionBuilderValidationResult validationResult = mock(VersionService.VersionBuilderValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        @SuppressWarnings("unchecked")
        ServiceOutcome<Version> serviceOutcome = mock(ServiceOutcome.class);
        when(serviceOutcome.isValid()).thenReturn(false);
        when(serviceOutcome.getErrorCollection()).thenReturn(errorCollection);

        when(mockVersionService.validateCreate(mockUser, mockReleaseBuilder)).thenReturn(validationResult);
        when(mockVersionService.create(mockUser, validationResult)).thenReturn(serviceOutcome);

        ImportResult importResult = releaseCreateStrategy.importRelease(mockUser, mockReleaseBuilder, null, "project");

        assertEquals(ResultType.RELEASE, importResult.getType());
        assertEquals(ResultState.ERROR, importResult.getState());
        assertEquals("create error", importResult.getValue());
    }

    @Test
    public void testImportRelease_validRelease_successResult() throws Exception {
        VersionService.VersionBuilderValidationResult validationResult = mock(VersionService.VersionBuilderValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        @SuppressWarnings("unchecked")
        ServiceOutcome<Version> serviceOutcome = mock(ServiceOutcome.class);
        when(serviceOutcome.isValid()).thenReturn(true);
        MockVersion release = new MockVersion(1L, "release");
        release.setReleaseDate(new Date());
        when(serviceOutcome.getReturnedValue()).thenReturn(release);

        when(mockVersionService.validateCreate(mockUser, mockReleaseBuilder)).thenReturn(validationResult);
        when(mockVersionService.create(mockUser, validationResult)).thenReturn(serviceOutcome);

        ImportResult importResult = releaseCreateStrategy.importRelease(mockUser, mockReleaseBuilder, null, "project");

        assertEquals(ResultType.RELEASE, importResult.getType());
        assertEquals(ResultState.CREATED, importResult.getState());
        assertEquals("release", importResult.getValue());
    }

    @Test
    public void testImportRelease_validRelease_updateStatusErrorResult() throws Exception {
        ErrorCollection errorCollection = mock(ErrorCollection.class);
        when(errorCollection.toString()).thenReturn("update status error");
        VersionService.VersionBuilderValidationResult validationResult = mock(VersionService.VersionBuilderValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        @SuppressWarnings("unchecked")
        ServiceOutcome<Version> serviceOutcome = mock(ServiceOutcome.class);
        when(serviceOutcome.isValid()).thenReturn(true);
        MockVersion release = new MockVersion(1L, "release");
        release.setReleaseDate(new Date());
        when(serviceOutcome.getReturnedValue()).thenReturn(release);
        VersionService.ReleaseVersionValidationResult releaseValidationResult = mock(VersionService.ReleaseVersionValidationResult.class);
        when(releaseValidationResult.isValid()).thenReturn(false);
        when(releaseValidationResult.getErrorCollection()).thenReturn(errorCollection);

        when(mockVersionService.validateCreate(mockUser, mockReleaseBuilder)).thenReturn(validationResult);
        when(mockVersionService.create(mockUser, validationResult)).thenReturn(serviceOutcome);
        when(mockVersionService.validateReleaseVersion(mockUser, release, release.getReleaseDate())).thenReturn(releaseValidationResult);

        ImportResult importResult = releaseCreateStrategy.importRelease(mockUser, mockReleaseBuilder, "closed", "project");

        assertEquals(ResultType.RELEASE, importResult.getType());
        assertEquals(ResultState.ERROR, importResult.getState());
        assertEquals("update status error", importResult.getValue());
    }

    @Test
    public void testImportRelease_validRelease_releaseDateErrorResult() throws Exception {
        VersionService.VersionBuilderValidationResult validationResult = mock(VersionService.VersionBuilderValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        @SuppressWarnings("unchecked")
        ServiceOutcome<Version> serviceOutcome = mock(ServiceOutcome.class);
        when(serviceOutcome.isValid()).thenReturn(true);
        Version release = new MockVersion(1L, "release");
        when(serviceOutcome.getReturnedValue()).thenReturn(release);

        when(mockVersionService.validateCreate(mockUser, mockReleaseBuilder)).thenReturn(validationResult);
        when(mockVersionService.create(mockUser, validationResult)).thenReturn(serviceOutcome);

        ImportResult importResult = releaseCreateStrategy.importRelease(mockUser, mockReleaseBuilder, "closed", "project");

        assertEquals(ResultType.RELEASE, importResult.getType());
        assertEquals(ResultState.ERROR, importResult.getState());
        assertEquals("Update release status error: release date must not be null.", importResult.getValue());
    }

    @Test
    public void testImportRelease_validRelease_updateStatusSuccessResult() throws Exception {
        VersionService.VersionBuilderValidationResult validationResult = mock(VersionService.VersionBuilderValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        @SuppressWarnings("unchecked")
        ServiceOutcome<Version> serviceOutcome = mock(ServiceOutcome.class);
        when(serviceOutcome.isValid()).thenReturn(true);
        MockVersion release = new MockVersion(1L, "release");
        release.setReleaseDate(new Date());
        when(serviceOutcome.getReturnedValue()).thenReturn(release);
        VersionService.ReleaseVersionValidationResult releaseValidationResult = mock(VersionService.ReleaseVersionValidationResult.class);
        when(releaseValidationResult.isValid()).thenReturn(true);

        when(mockVersionService.validateCreate(mockUser, mockReleaseBuilder)).thenReturn(validationResult);
        when(mockVersionService.create(mockUser, validationResult)).thenReturn(serviceOutcome);
        when(mockVersionService.validateReleaseVersion(mockUser, release, release.getReleaseDate())).thenReturn(releaseValidationResult);
        when(mockVersionService.releaseVersion(releaseValidationResult)).thenReturn(release);

        ImportResult importResult = releaseCreateStrategy.importRelease(mockUser, mockReleaseBuilder, "closed", "project");

        assertEquals(ResultType.RELEASE, importResult.getType());
        assertEquals(ResultState.CREATED, importResult.getState());
        assertEquals("release", importResult.getValue());
    }
}
