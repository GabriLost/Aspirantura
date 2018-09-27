package ru.sbertech.atlas.jira.cupintegration.out.xmlview;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.statistics.util.FieldableDocumentHitCollector;
import com.atlassian.jira.issue.views.SingleIssueWriter;
import com.atlassian.jira.issue.views.util.SearchRequestViewBodyWriterUtil;
import com.atlassian.jira.plugin.issueview.AbstractIssueView;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.web.component.IssueTableWriter;
import ru.sbertech.atlas.jira.cupintegration.out.collector.IssueInfoMapperHitCollector;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Dmitriy Klabukov
 */
public class ExtendedSearchRequestViewBodyWriterUtil implements SearchRequestViewBodyWriterUtil {
    private final IssueFactory issueFactory;
    private final JiraAuthenticationContext authenticationContext;
    private final SearchProvider searchProvider;

    public ExtendedSearchRequestViewBodyWriterUtil(final IssueFactory issueFactory,
        final JiraAuthenticationContext authenticationContext, final SearchProvider searchProvider) {
        this.issueFactory = issueFactory;
        this.authenticationContext = authenticationContext;
        this.searchProvider = searchProvider;
    }

    @Override
    public void writeBody(final Writer writer, final AbstractIssueView issueView, SearchRequest searchRequest, final SingleIssueWriter singleIssueWriter, PagerFilter pagerFilter)
        throws IOException, SearchException {
        final FieldableDocumentHitCollector hitCollector = new IssueInfoMapperHitCollector(writer, issueFactory) {
            @Override
            protected void writeIssue(Issue issue, Writer writer)  throws IOException {
                singleIssueWriter.writeIssue(issue,issueView, writer);
            }
        };

        searchProvider.searchAndSortOverrideSecurity((searchRequest != null) ? searchRequest.getQuery() : null, authenticationContext.getLoggedInUser(), hitCollector, pagerFilter);
    }

    @Override
    public void writeTableBody(Writer writer, final IssueTableWriter issueTableWriter, SearchRequest searchRequest, PagerFilter pagerFilter) throws IOException, SearchException {
        final FieldableDocumentHitCollector hitCollector = new IssueInfoMapperHitCollector(writer, issueFactory) {
            @Override
            protected void writeIssue(Issue issue, Writer writer)  throws IOException {
                issueTableWriter.write(issue);
            }
        };
        searchProvider.searchAndSortOverrideSecurity((searchRequest != null) ? searchRequest.getQuery() : null, authenticationContext.getLoggedInUser(), hitCollector, pagerFilter);
        issueTableWriter.close();
    }

    @Override
    public long searchCount(SearchRequest searchRequest) throws SearchException {
        return searchProvider.searchCountOverrideSecurity((searchRequest != null) ? searchRequest.getQuery() : null, authenticationContext.getUser());
    }
}
