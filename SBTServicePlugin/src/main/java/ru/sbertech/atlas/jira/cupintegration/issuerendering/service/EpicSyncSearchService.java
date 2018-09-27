package ru.sbertech.atlas.jira.cupintegration.issuerendering.service;


import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.query.Query;

import java.util.Set;

public interface EpicSyncSearchService {
    /**
     * return empty set if throws SearchException
     *
     * @param query JQL query
     * @return Set of issues that was found by JQL
     */
    Set<Issue> getIssueByQuery(Query query) throws SearchException;

    Query buildQueryByEpicId(String epcId);

    Query buildQueryByEpicIdTimeSpentEmpty(String epicId);
}
