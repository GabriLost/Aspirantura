package ru.sbertech.atlas.jira.cupintegration.out.worklog.jql;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogManager;
import com.atlassian.jira.jql.operand.QueryLiteral;
import com.atlassian.jira.jql.query.QueryCreationContext;
import com.atlassian.jira.jql.util.JqlDateSupport;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.query.operand.FunctionOperand;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WorklogPeriodJqlFunction.class, LoggerFactory.class})
public class WorklogPeriodJqlFunctionTest {
    private static Logger mockLog;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setup() {
        mockLog = Mockito.mock(Logger.class);
        PowerMockito.mockStatic(LoggerFactory.class);
        when(LoggerFactory.getLogger(WorklogPeriodJqlFunction.class)).thenReturn(mockLog);
    }

    @Test
    public void testGetValues_ValidData_ListLiterals() throws Exception {
        reset(mockLog);
        FunctionOperand functionOperand = mock(FunctionOperand.class);
        List<String> operands = new ArrayList<>(Arrays.asList("project = TEST", "2016-12-03", "2016-12-10"));
        when(functionOperand.getArgs()).thenReturn(operands);
        JqlDateSupport jqlDateSupport = mock(JqlDateSupport.class);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        when(jqlDateSupport.convertToDate(operands.get(1), TimeZone.getDefault())).thenReturn(simpleDateFormat.parse(operands.get(1)));
        when(jqlDateSupport.convertToDate(operands.get(2), TimeZone.getDefault())).thenReturn(simpleDateFormat.parse(operands.get(2)));
        SearchProvider searchProvider = mock(SearchProvider.class);
        QueryCreationContext queryCreationContext = mock(QueryCreationContext.class);
        ApplicationUser user = new MockApplicationUser("MockUser");
        when(queryCreationContext.getApplicationUser()).thenReturn(user);
        SearchResults searchResult = mock(SearchResults.class);
        Issue is1 = new MockIssue(1, "Issue-1");
        Issue is2 = new MockIssue(2, "Issue-2");
        when(searchResult.getTotal()).thenReturn(2);
        when(searchResult.getIssues()).thenReturn(Arrays.asList(is1, is2));
        when(searchProvider.searchOverrideSecurity(any(Query.class), any(ApplicationUser.class), any(PagerFilter.class), any(org.apache.lucene.search.Query.class)))
            .thenReturn(searchResult);
        WorklogManager workLogManager = mock(WorklogManager.class);
        Worklog worklog1 = mock(Worklog.class);
        when(worklog1.getStartDate()).thenReturn(simpleDateFormat.parse("2016-12-06"));
        when(workLogManager.getByIssue(is1)).thenReturn(Collections.singletonList(worklog1));
        Worklog worklog2 = mock(Worklog.class);
        when(worklog2.getStartDate()).thenReturn(simpleDateFormat.parse("2015-12-06"));
        when(workLogManager.getByIssue(is2)).thenReturn(Collections.singletonList(worklog2));
        WorklogPeriodJqlFunction worklogPeriodJqlFunction = new WorklogPeriodJqlFunction(null, jqlDateSupport, searchProvider, workLogManager);

        List<QueryLiteral> actual = worklogPeriodJqlFunction.getValues(queryCreationContext, functionOperand, null);

        verify(mockLog, times(1)).debug("Performing worklog period search with args [project = TEST, 2016-12-03, 2016-12-10]");
        verify(mockLog, times(1)).debug("Jql function worklogPeriod starts with param: timespent != null AND project = TEST");
        verify(mockLog, times(1)).debug("Search Result Count: 2");
        verify(mockLog, times(1)).debug("Returning literals: 1");
        assertEquals(new Long(1l), actual.get(0).getLongValue());
        assertEquals(1, actual.size());
    }

    @Test
    public void testGetValues_MissedArguments_Exception() throws Exception {
        reset(mockLog);
        FunctionOperand functionOperand = mock(FunctionOperand.class);
        List<String> operands = new ArrayList<>(Arrays.asList("project = TEST", "2016-12-03"));
        when(functionOperand.getArgs()).thenReturn(operands);
        WorklogPeriodJqlFunction worklogPeriodJqlFunction = new WorklogPeriodJqlFunction(null, null, null, null);

        exception.expectMessage("Missed arguments, must be 3");
        worklogPeriodJqlFunction.getValues(null, functionOperand, null);
        verify(mockLog, times(1)).error("Missed arguments, must be 3");
    }

    @Test
    public void testGetValues_InvalidJql_LogError() throws Exception {
        reset(mockLog);
        FunctionOperand functionOperand = mock(FunctionOperand.class);
        List<String> operands = new ArrayList<>(Arrays.asList("invalid jql", "2016-12-03", "2016-12-10"));
        when(functionOperand.getArgs()).thenReturn(operands);
        JqlDateSupport jqlDateSupport = mock(JqlDateSupport.class);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        when(jqlDateSupport.convertToDate(operands.get(1), TimeZone.getDefault())).thenReturn(simpleDateFormat.parse(operands.get(1)));
        when(jqlDateSupport.convertToDate(operands.get(2), TimeZone.getDefault())).thenReturn(simpleDateFormat.parse(operands.get(2)));

        WorklogPeriodJqlFunction worklogPeriodJqlFunction = new WorklogPeriodJqlFunction(null, jqlDateSupport, null, null);

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Jql function worklogPeriod parse error");

        worklogPeriodJqlFunction.getValues(null, functionOperand, null);

        verify(mockLog, times(1)).error("Something went wrong with the search. Aborting. [Jql function worklogPeriod parse error]", any(IllegalArgumentException.class));
    }
}