package ru.sbertech.atlas.jira.cupintegration.issuerendering.service;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.usercompatibility.UserCompatibilityHelper;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.operator.Operator;

import java.util.HashSet;
import java.util.Set;

public class EpicSyncSearchServiceImpl implements EpicSyncSearchService {
    private final SearchService searchService;

    public EpicSyncSearchServiceImpl(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public Set<Issue> getIssueByQuery(Query query) throws SearchException {
        final SearchResults results = searchService.search(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), query, PagerFilter.getUnlimitedFilter());
        return new HashSet<>(results.getIssues());
    }

    @Override
    public Query buildQueryByEpicId(String epicId) {
        return JqlQueryBuilder.newClauseBuilder().addStringCondition("Epic link", Operator.EQUALS, epicId).buildQuery();
    }

    @Override
    public Query buildQueryByEpicIdTimeSpentEmpty(String epicId) {
        return JqlQueryBuilder.newClauseBuilder().addStringCondition("Epic link", Operator.EQUALS, epicId).defaultAnd().timeSpent().isEmpty().defaultOr().timeSpent().eqEmpty().defaultOr().timeSpent().eq("0").buildQuery();
    }
}
