package ru.sbertech.atlas.jira.cupintegration.issuerendering.processor;

import com.atlassian.jira.issue.*;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.query.Query;
import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.service.ImportSettingService;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.model.EpicSyncResult;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.service.EpicSyncSearchService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class EpicIssueSyncProcessorImplTest {

    @Test
    public void testUpdateIssues() throws Exception {
        CustomFieldManager customFieldManager = mock(CustomFieldManager.class);
        CustomField customField = mock(CustomField.class);
        when(customFieldManager.getCustomFieldObject(100L)).thenReturn(customField);
        IssueManager issueManager = mock(IssueManager.class);
        Issue epic = new MockIssue();
        when(issueManager.getIssueObject("epic")).thenReturn((MutableIssue) epic);
        when(customField.getValue(epic)).thenReturn("1122");
        ImportSettingService importSettingService = mock(ImportSettingService.class);
        when(importSettingService.getImportSetting()).thenReturn(new ImportSettings(null, null, false, null, null, "100"));
        EpicSyncSearchService epicSyncSearchService = mock(EpicSyncSearchService.class);
        //issue key must contains a dash symbol
        Issue issue1 = new MockIssue(1, "s-1");
        Issue issue2 = new MockIssue(2, "s-2");
        Issue issue3 = new MockIssue(3, "s-3");
        when(epicSyncSearchService.getIssueByQuery((Query) anyObject())).thenReturn(new HashSet<>(Arrays.asList(issue1, issue2, issue3)));
        String[] selectedIssues = {"s-1", "s-2"};

        EpicIssueSyncProcessorImpl issueSyncProcessor = new EpicIssueSyncProcessorImpl(customFieldManager, importSettingService, epicSyncSearchService, issueManager);
        Set<EpicSyncResult> epicSyncResults = issueSyncProcessor.updateIssues("epic", selectedIssues);

        verify(customFieldManager, times(1)).getCustomFieldObject(eq(100L));
        verify(customField, times(2)).updateValue((FieldLayoutItem) anyObject(), (Issue) anyObject(), (ModifiedValue) anyObject(), (DefaultIssueChangeHolder) anyObject());
        assertEquals(epicSyncResults.size(), 2);

    }
}
