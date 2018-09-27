package ru.sbertech.atlas.jira.cupintegration.out.worklog.jql;

import com.atlassian.jira.JiraDataType;
import com.atlassian.jira.JiraDataTypes;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogManager;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.jql.parser.DefaultJqlQueryParser;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.query.QueryCreationContext;
import com.atlassian.jira.jql.util.JqlDateSupport;
import com.atlassian.jira.plugin.jql.function.AbstractJqlFunction;
import com.atlassian.jira.plugin.jql.function.JqlFunction;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.MessageSet;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.clause.TerminalClause;
import com.atlassian.query.operand.FunctionOperand;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.slf4j.Logger;
import ru.sbertech.atlas.jira.cupintegration.in.service.ImportSettingService;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Dmitriy Klabukov
 */
public final class WorklogPeriodJqlFunction extends AbstractJqlFunction implements JqlFunction {

    private static final Logger log = getLogger(WorklogPeriodJqlFunction.class);
    private final ImportSettingService importSettingService;
    private JqlDateSupport jqlDateSupport;
    private SearchProvider searchProvider;
    private WorklogManager worklogManager;

    public WorklogPeriodJqlFunction(ImportSettingService importSettingService, JqlDateSupport jqlDateSupport, SearchProvider searchProvider, WorklogManager worklogManager) {
        this.importSettingService = importSettingService;
        this.jqlDateSupport = jqlDateSupport;
        this.searchProvider = searchProvider;
        this.worklogManager = worklogManager;
    }

    @Nonnull
    @Override
    public MessageSet validate(ApplicationUser searcher, @Nonnull FunctionOperand operand, @Nonnull TerminalClause terminalClause) {
        return null;
    }

    @Nonnull
    @Override
    public List<QueryLiteral> getValues(@Nonnull QueryCreationContext queryCreationContext, @Nonnull FunctionOperand operand, @Nonnull TerminalClause terminalClause) {
        log.debug("Performing worklog period search with args " + operand.getArgs());
        ArrayList<QueryLiteral> literals = new ArrayList<>();
        List args = operand.getArgs();
        if (args.isEmpty() || args.size() != 3) {
            log.error("Missed arguments, must be 3");
            throw new IllegalArgumentException("Missed arguments, must be 3");
        }
        String jqlQuery = (String) args.get(0);
        Date min = this.jqlDateSupport.convertToDate((String) args.get(1), TimeZone.getDefault());
        Date max = this.jqlDateSupport.convertToDate((String) args.get(2), TimeZone.getDefault());
        if (!StringUtils.isEmpty(jqlQuery)) {
            jqlQuery = " AND " + jqlQuery;
        }
        if (min != null && max != null) {
            List<Long> issueIds = new ArrayList<>();

            Query query = null;
            try {
                query = new DefaultJqlQueryParser().parseQuery("timespent != null" + jqlQuery);
                log.debug("Jql function worklogPeriod starts with param: " + query.getQueryString());
            } catch (JqlParseException e) {
                log.error("Jql function worklogPeriod parse error", e);
                throw new IllegalArgumentException("Jql function worklogPeriod parse error");
            }

            try {
                SearchResults searchResults = this.searchProvider.searchOverrideSecurity(query, queryCreationContext.getApplicationUser(), PagerFilter.getUnlimitedFilter(), null);
                log.debug("Search Result Count: " + searchResults.getTotal());

                for (Issue issue : searchResults.getIssues()) {

                    List<Worklog> workLogs = worklogManager.getByIssue(issue);
                    boolean inPeriod = false;
                    for (Worklog worklog : workLogs) {
                        if (new Interval(new DateTime(min), new DateTime(max)).contains(new DateTime(worklog.getStartDate()))) {
                            inPeriod = true;
                            break;
                        }
                    }

                    if (inPeriod) {
                        issueIds.add(issue.getId());
                    }
                }
            } catch (SearchException ex) {
                log.error("Something went wrong with the search. Aborting. [" + ex.getMessage() + "]", ex);
                throw new RuntimeException(ex);
            }

            for (Object issueId : issueIds) {
                Long key = (Long) issueId;
                literals.add(new QueryLiteral(operand, key));
            }
        }


        log.debug("Returning literals: " + literals.size());
        return literals;
    }

    @Override
    public int getMinimumNumberOfExpectedArguments() {
        return 3;
    }

    @Nonnull
    @Override
    public JiraDataType getDataType() {
        return JiraDataTypes.ISSUE;
    }
}
