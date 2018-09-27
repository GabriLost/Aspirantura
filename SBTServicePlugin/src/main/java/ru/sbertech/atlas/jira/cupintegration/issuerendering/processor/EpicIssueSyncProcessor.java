package ru.sbertech.atlas.jira.cupintegration.issuerendering.processor;

import com.atlassian.jira.issue.search.SearchException;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.model.EpicSyncResult;

import java.util.Set;

public interface EpicIssueSyncProcessor {
    Set<EpicSyncResult> updateIssues(String idEpic, String[] selectedIssueIds) throws SearchException;
}
