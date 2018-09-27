package ru.sbertech.atlas.jira.cupintegration.in.issue;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateIssueStrategyTest {

    @Test
    public void testImportIssue_invalidIssueInputParameters_importResultWithValidationError() throws Exception {

        ErrorCollection errorCollection = mock(ErrorCollection.class);
        when(errorCollection.toString()).thenReturn("validation error");
        IssueInputParameters issueInputParameters = mock(IssueInputParameters.class);
        IssueService.UpdateValidationResult validationResult = mock(IssueService.UpdateValidationResult.class);
        when(validationResult.isValid()).thenReturn(false);
        when(validationResult.getErrorCollection()).thenReturn(errorCollection);
        ApplicationUser user = mock(ApplicationUser.class);
        IssueService issueService = mock(IssueService.class);
        Long issueId = 1L;
        when(issueService.validateUpdate(user, issueId, issueInputParameters)).thenReturn(validationResult);
        UpdateStrategy updateIssueStrategy = new UpdateStrategy(issueService);
        ImportResult importResult = updateIssueStrategy.importIssue(user, issueInputParameters, issueId);

        assertEquals(ResultType.ISSUE, importResult.getType());
        assertEquals(ResultState.ERROR, importResult.getState());
        assertEquals("validation error", importResult.getValue());
    }

    @Test
    public void testImportIssue_validIssueInputParameters_importResultWithCreationError() throws Exception {

        IssueInputParameters issueInputParameters = mock(IssueInputParameters.class);
        IssueService.UpdateValidationResult validationResult = mock(IssueService.UpdateValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        ApplicationUser user = mock(ApplicationUser.class);
        IssueService issueService = mock(IssueService.class);
        Long issueId = 1L;
        when(issueService.validateUpdate(user, issueId, issueInputParameters)).thenReturn(validationResult);
        IssueService.IssueResult issueResult = mock(IssueService.IssueResult.class);
        when(issueResult.isValid()).thenReturn(false);
        ErrorCollection errorCollection = mock(ErrorCollection.class);
        when(errorCollection.toString()).thenReturn("updation error");
        when(issueResult.getErrorCollection()).thenReturn(errorCollection);
        when(issueService.update(user, validationResult)).thenReturn(issueResult);
        UpdateStrategy updateIssueStrategy = new UpdateStrategy(issueService);
        ImportResult importResult = updateIssueStrategy.importIssue(user, issueInputParameters, issueId);

        assertEquals(ResultType.ISSUE, importResult.getType());
        assertEquals(ResultState.ERROR, importResult.getState());
        assertEquals("updation error", importResult.getValue());
    }

    @Test
    public void testImportIssue_validIssueInputParameters_importResultWithIssueKey() throws Exception {

        IssueInputParameters issueInputParameters = mock(IssueInputParameters.class);
        IssueService.UpdateValidationResult validationResult = mock(IssueService.UpdateValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);
        ApplicationUser user = mock(ApplicationUser.class);
        IssueService issueService = mock(IssueService.class);
        Long issueId = 1L;
        when(issueService.validateUpdate(user, issueId, issueInputParameters)).thenReturn(validationResult);
        IssueService.IssueResult issueResult = mock(IssueService.IssueResult.class);
        when(issueResult.isValid()).thenReturn(true);
        MockIssue issue = new MockIssue();
        issue.setKey("TEST-1");
        when(issueResult.getIssue()).thenReturn(issue);
        when(issueService.update(user, validationResult)).thenReturn(issueResult);
        UpdateStrategy updateIssueStrategy = new UpdateStrategy(issueService);
        ImportResult importResult = updateIssueStrategy.importIssue(user, issueInputParameters, issueId);

        assertEquals(ResultType.ISSUE, importResult.getType());
        assertEquals(ResultState.UPDATED, importResult.getState());
        assertEquals("TEST-1", importResult.getValue());
    }
}
