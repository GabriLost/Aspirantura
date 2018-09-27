package ru.sbertech.atlas.jira.cupintegration.issuerendering.webaction;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockUser;
import com.atlassian.jira.usercompatibility.UserCompatibilityHelper;
import com.atlassian.jira.usercompatibility.UserWithKey;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.model.EpicSyncResult;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.processor.EpicIssueSyncProcessor;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.service.EpicSyncSearchService;

import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Created by Yaroslav Astafiev on 22/03/2016.
 * Department of analytical solutions and system services improvement.
 */
@RunWith(PowerMockRunner.class)
public class EpicSyncActionTest {

    private static EpicIssueSyncProcessor epicIssueSyncProcessor;
    private static EpicSyncSearchService epicSyncSearchService;
    private static Set<Issue> issuesSet;

    @BeforeClass
    public static void init() throws Exception {
        issuesSet = Collections.singleton(mock(Issue.class));

        epicIssueSyncProcessor = mock(EpicIssueSyncProcessor.class);
        epicSyncSearchService = mock(EpicSyncSearchService.class);
        when(epicSyncSearchService.buildQueryByEpicId(anyString())).thenReturn(null);
        when(epicSyncSearchService.getIssueByQuery(null)).thenReturn(issuesSet);
    }

    @Test
    public void testDoDefault_ReturnInput() throws Exception {
        EpicSyncAction epicSyncAction = new EpicSyncAction(epicIssueSyncProcessor, epicSyncSearchService);

        assertEquals("input", epicSyncAction.doDefault());
    }

    @Test
    @PrepareForTest({UserCompatibilityHelper.class, ComponentAccessor.class})
    public void testDoDefault_ReturnSuccess() throws Exception {
        reset(epicIssueSyncProcessor);

        UserWithKey userWithKey = mock(UserWithKey.class);
        when(userWithKey.getUser()).thenReturn(null);
        PowerMockito.mockStatic(UserCompatibilityHelper.class);
        when(UserCompatibilityHelper.convertUserObject(any(ApplicationUser.class))).thenReturn(userWithKey);

        JiraAuthenticationContext jiraAuthenticationContext = mock(JiraAuthenticationContext.class);
        when(jiraAuthenticationContext.getUser()).thenReturn(null);
        PowerMockito.mockStatic(ComponentAccessor.class);
        when(ComponentAccessor.getJiraAuthenticationContext()).thenReturn(jiraAuthenticationContext);

        Set<EpicSyncResult> epicSyncResultSet = Collections.singleton(new EpicSyncResult(new MockIssue(), "1000_old", "10001_new"));
        when(epicIssueSyncProcessor.updateIssues(anyString(), any(String[].class))).thenReturn(epicSyncResultSet);

        EpicSyncAction epicSyncAction = new EpicSyncAction(epicIssueSyncProcessor, epicSyncSearchService);
        epicSyncAction.setSelectedIssueIds(new String[] {"1"});

        assertEquals("success", epicSyncAction.doDefault());
        assertEquals(epicSyncResultSet, epicSyncAction.getEpicSyncResults());
        assertEquals(issuesSet, epicSyncAction.getIssues());
        verify(epicIssueSyncProcessor, times(1)).updateIssues(anyString(), any(String[].class));
    }

    @Test
    @PrepareForTest({UserCompatibilityHelper.class, ComponentAccessor.class})
    public void testDoDefault_ReturnSuccess_SignedUser() throws Exception {
        reset(epicIssueSyncProcessor);

        UserWithKey userWithKey = mock(UserWithKey.class);
        when(userWithKey.getUser()).thenReturn(new MockUser("SomeName"));
        PowerMockito.mockStatic(UserCompatibilityHelper.class);
        when(UserCompatibilityHelper.convertUserObject(any(ApplicationUser.class))).thenReturn(userWithKey);

        JiraAuthenticationContext jiraAuthenticationContext = mock(JiraAuthenticationContext.class);
        when(jiraAuthenticationContext.getUser()).thenReturn(null);
        PowerMockito.mockStatic(ComponentAccessor.class);
        when(ComponentAccessor.getJiraAuthenticationContext()).thenReturn(jiraAuthenticationContext);

        Set<EpicSyncResult> epicSyncResultSet = Collections.singleton(new EpicSyncResult(new MockIssue(), "1000_old", "10001_new"));
        when(epicIssueSyncProcessor.updateIssues(anyString(), any(String[].class))).thenReturn(epicSyncResultSet);

        EpicSyncAction epicSyncAction = new EpicSyncAction(epicIssueSyncProcessor, epicSyncSearchService);
        epicSyncAction.setSelectedIssueIds(new String[] {"1"});

        assertEquals("success", epicSyncAction.doDefault());
        assertEquals(epicSyncResultSet, epicSyncAction.getEpicSyncResults());
        assertEquals(issuesSet, epicSyncAction.getIssues());
        verify(epicIssueSyncProcessor, times(1)).updateIssues(anyString(), any(String[].class));
    }

    @Test
    public void testDoDefault_ReturnError() throws Exception {
        EpicSyncAction epicSyncAction = new EpicSyncAction(epicIssueSyncProcessor, epicSyncSearchService);
        epicSyncAction.setSelectedIssueIds(new String[] {"1"});

        assertEquals("error", epicSyncAction.doDefault());
        assertEquals(issuesSet, epicSyncAction.getIssues());
    }

    @Test
    public void testGetters() throws Exception {
        EpicSyncAction epicSyncAction = new EpicSyncAction(epicIssueSyncProcessor, epicSyncSearchService);

        assertNull(epicSyncAction.getEpicId());
        assertNull(epicSyncAction.getEpicSyncResults());
        assertNull(epicSyncAction.getIssues());
        assertNull(epicSyncAction.getSelectedIssueIds());
    }

    @Test
    public void testSetters() throws Exception {
        EpicSyncAction epicSyncAction = new EpicSyncAction(epicIssueSyncProcessor, epicSyncSearchService);

        assertNull(epicSyncAction.getEpicId());
        assertNull(epicSyncAction.getEpicSyncResults());
        assertNull(epicSyncAction.getIssues());
        assertNull(epicSyncAction.getSelectedIssueIds());

        String[] expectedIds = new String[] {"1"};
        String expectedEpicId = "2";
        epicSyncAction.setSelectedIssueIds(expectedIds);

        assertArrayEquals(expectedIds, epicSyncAction.getSelectedIssueIds());

        epicSyncAction.setEpicId(expectedEpicId);
        assertEquals(expectedEpicId, epicSyncAction.getEpicId());
    }

}
