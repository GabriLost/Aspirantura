package ru.sbertech.atlas.jira.cupintegration.out.action.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.JiraAuthenticationContextImpl;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.exception.RequiredComponentNotFoundException;
import ru.sbertech.atlas.jira.cupintegration.out.TimesheetGenerator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dmitriev Vladimir
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TimesheetGenerator.class, ComponentAccessor.class})
public class TimesheetServiceTest {
    private static final String FROM_DATE = "01.02.2016";
    private static final String TO_DATE = "29.02.2016";
    private static final String FILTER = "project = TEST";

    private TimesheetGenerator timesheetGenerator;
    private TimesheetService timesheetService;

    @Before
    public void setup() {
        timesheetGenerator = PowerMockito.mock(TimesheetGenerator.class);
        timesheetService = new TimesheetService(timesheetGenerator);
    }


    @Test(expected = RequiredComponentNotFoundException.class)
    public void testGetXmlTimeSheet_withArguments_RequiredComponentNotFoundException() throws Exception {
        when(timesheetGenerator.buildXMLAsync(FROM_DATE, TO_DATE, FILTER)).thenThrow(new RequiredComponentNotFoundException(""));

        timesheetService.getXmlTimeSheet(FROM_DATE, TO_DATE, FILTER);
    }


    @Test(expected = ExecutionException.class)
    public void testGetXmlTimeSheet_withArguments_ExecutionException() throws Exception {
        @SuppressWarnings("unchecked")
        Future<String> future = mock(FutureTask.class);
        when(future.get()).thenThrow(new ExecutionException("TestThrowable", new Exception()));

        when(timesheetGenerator.buildXMLAsync(FROM_DATE, TO_DATE, FILTER)).thenReturn(future);

        timesheetService.getXmlTimeSheet(FROM_DATE, TO_DATE, FILTER);
    }

    @Test(expected = InterruptedException.class)
    public void testGetXmlTimeSheet_withArguments_InterruptedException() throws Exception {
        @SuppressWarnings("unchecked")
        Future<String> future = mock(FutureTask.class);
        when(future.get()).thenThrow(new InterruptedException());

        when(timesheetGenerator.buildXMLAsync(FROM_DATE, TO_DATE, FILTER)).thenReturn(future);

        timesheetService.getXmlTimeSheet(FROM_DATE, TO_DATE, FILTER);
    }

    @Test
    public void testGetXmlTimeSheet_withNullArguments_okResponse() throws Exception {
        @SuppressWarnings("unchecked")
        Future<String> future = mock(FutureTask.class);
        when(future.get()).thenReturn("test");

        when(timesheetGenerator.buildXMLAsync(null, null, null)).thenReturn(future);

        Response response = timesheetService.getXmlTimeSheet(null, null, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetXmlTimeSheet_withArguments_okResponse() throws Exception {
        @SuppressWarnings("unchecked")
        Future<String> future = mock(FutureTask.class);
        when(future.get()).thenReturn("test");

        when(timesheetGenerator.buildXMLAsync(FROM_DATE, TO_DATE, FILTER)).thenReturn(future);

        Response response = timesheetService.getXmlTimeSheet(FROM_DATE, TO_DATE, FILTER);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testRegisterTimesheetExport_withNullArguments_okResponse() throws Exception {
        JiraAuthenticationContext jiraAuthenticationContext = mock(JiraAuthenticationContextImpl.class);
        when(jiraAuthenticationContext.getUser()).thenReturn(new MockApplicationUser("testUser"));

        PowerMockito.mockStatic(ComponentAccessor.class);
        PowerMockito.when(ComponentAccessor.getJiraAuthenticationContext()).thenReturn(jiraAuthenticationContext);

        doNothing().when(timesheetGenerator).generateXmlAndStore(anyString(), anyString(), anyString(), any(ApplicationUser.class));

        Response response = timesheetService.registerTimesheetExport(null, null, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testRegisterTimesheetExport_withArguments_okResponse() throws Exception {
        JiraAuthenticationContext jiraAuthenticationContext = mock(JiraAuthenticationContextImpl.class);
        when(jiraAuthenticationContext.getUser()).thenReturn(new MockApplicationUser("testUser"));

        PowerMockito.mockStatic(ComponentAccessor.class);
        PowerMockito.when(ComponentAccessor.getJiraAuthenticationContext()).thenReturn(jiraAuthenticationContext);

        doNothing().when(timesheetGenerator).generateXmlAndStore(anyString(), anyString(), anyString(), any(ApplicationUser.class));

        Response response = timesheetService.registerTimesheetExport(FROM_DATE, TO_DATE, FILTER);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = RequiredComponentNotFoundException.class)
    public void testRegisterTimesheetExport_withArguments_RequiredComponentNotFoundException() throws Exception {
        JiraAuthenticationContext jiraAuthenticationContext = mock(JiraAuthenticationContextImpl.class);
        when(jiraAuthenticationContext.getUser()).thenReturn(new MockApplicationUser("testUser"));

        PowerMockito.mockStatic(ComponentAccessor.class);
        PowerMockito.when(ComponentAccessor.getJiraAuthenticationContext()).thenReturn(jiraAuthenticationContext);

        doThrow(new RequiredComponentNotFoundException("")).when(timesheetGenerator).generateXmlAndStore(anyString(), anyString(), anyString(), any(ApplicationUser.class));

        timesheetService.registerTimesheetExport(FROM_DATE, TO_DATE, FILTER);
    }
}
