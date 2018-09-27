package ru.sbertech.atlas.jira.cupintegration.out.utils;

import com.atlassian.jira.jql.builder.JqlClauseBuilderFactory;
import com.atlassian.jira.jql.builder.JqlClauseBuilderFactoryImpl;
import com.atlassian.jira.jql.util.JqlDateSupportImpl;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.timezone.TimeZoneManager;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Dmitriy Klabukov
 */
public class TimesheetGeneratorUtilsTest {

    @Test
    public void testBuildQueryFromFilterAndDates_EmptyFilter_QueryWithDates() throws Exception {
        TimeZoneManager timeZoneManager = Mockito.mock(TimeZoneManager.class);
        Mockito.when(timeZoneManager.getLoggedInUserTimeZone()).thenReturn(TimeZone.getDefault());
        new MockComponentWorker().addMock(JqlClauseBuilderFactory.class, new JqlClauseBuilderFactoryImpl(new JqlDateSupportImpl(timeZoneManager))).init();

        String query = TimesheetGeneratorUtils.buildQueryFromFilterAndDates("2015-12-12", "2015-12-13", "").getWhereClause().toString();

        assertEquals("{key in worklogPeriod(, 2015-12-12, 2015-12-13)}", query);
    }

    @Test
    public void testBuildQueryFromFilterAndDates_NullFilter_QueryWithDates() throws Exception {
        TimeZoneManager timeZoneManager = Mockito.mock(TimeZoneManager.class);
        Mockito.when(timeZoneManager.getLoggedInUserTimeZone()).thenReturn(TimeZone.getDefault());
        new MockComponentWorker().addMock(JqlClauseBuilderFactory.class, new JqlClauseBuilderFactoryImpl(new JqlDateSupportImpl(timeZoneManager))).init();

        String query = TimesheetGeneratorUtils.buildQueryFromFilterAndDates("2015-12-12", "2015-12-13", null).getWhereClause().toString();

        assertEquals("{key in worklogPeriod(, 2015-12-12, 2015-12-13)}", query);
    }

    @Test
    public void testBuildQueryFromFilterAndDates_NoEmptyCorrectFilter_QueryWithDates() throws Exception {
        TimeZoneManager timeZoneManager = Mockito.mock(TimeZoneManager.class);
        Mockito.when(timeZoneManager.getLoggedInUserTimeZone()).thenReturn(TimeZone.getDefault());
        new MockComponentWorker().addMock(JqlClauseBuilderFactory.class, new JqlClauseBuilderFactoryImpl(new JqlDateSupportImpl(timeZoneManager))).init();

        String query = TimesheetGeneratorUtils.buildQueryFromFilterAndDates("2015-12-12", "2015-12-13", "status = Resolved").getWhereClause().toString();

        assertEquals("{key in worklogPeriod(status = Resolved, 2015-12-12, 2015-12-13)}", query);
    }

    @Test
    public void testDefaultConstructor() throws Exception {
        assertNotNull(new TimesheetGeneratorUtils());
    }
}
