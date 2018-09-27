package ru.sbertech.atlas.jira.cupintegration.condition;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Yaroslav Astafiev on 18/03/2016.
 * Department of analytical solutions and system services improvement.
 */
public class EpicConditionTest {

    @Test
    public void shouldDisplay_EpicIssue() throws Exception {
        IssueType issueType = mock(IssueType.class);
        when(issueType.getName()).thenReturn("Epic");
        Issue issue = mock(Issue.class);
        when(issue.getIssueTypeObject()).thenReturn(issueType);
        JiraHelper jiraHelper = mock(JiraHelper.class);
        Map<String, Object> map = new HashMap<>();
        map.put("issue", issue);

        when(jiraHelper.getContextParams()).thenReturn(map);

        assertTrue(new EpicCondition().shouldDisplay(null, jiraHelper));
    }

    @Test
    public void shouldDisplay_NotEpicIssue() throws Exception {
        IssueType issueType = mock(IssueType.class);
        when(issueType.getName()).thenReturn("Task");
        Issue issue = mock(Issue.class);
        when(issue.getIssueTypeObject()).thenReturn(issueType);
        JiraHelper jiraHelper = mock(JiraHelper.class);
        Map<String, Object> map = new HashMap<>();
        map.put("issue", issue);

        when(jiraHelper.getContextParams()).thenReturn(map);

        assertFalse(new EpicCondition().shouldDisplay(null, jiraHelper));
    }

    @Test
    public void shouldDisplay_NullIssue() throws Exception {
        JiraHelper jiraHelper = mock(JiraHelper.class);
        Map<String, Object> map = new HashMap<>();
        map.put("issue", null);

        when(jiraHelper.getContextParams()).thenReturn(map);

        assertFalse(new EpicCondition().shouldDisplay(null, jiraHelper));
    }
}
