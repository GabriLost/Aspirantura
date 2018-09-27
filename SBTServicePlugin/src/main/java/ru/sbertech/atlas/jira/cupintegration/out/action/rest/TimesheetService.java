package ru.sbertech.atlas.jira.cupintegration.out.action.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import ru.sbertech.atlas.jira.cupintegration.exception.RequiredComponentNotFoundException;
import ru.sbertech.atlas.jira.cupintegration.out.TimesheetGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

@Path("/timesheet")
public class TimesheetService {

    private TimesheetGenerator timesheetGenerator;

    public TimesheetService(TimesheetGenerator timesheetGenerator) {
        this.timesheetGenerator = timesheetGenerator;
    }

    @POST
    @Path("getXmlTimeSheet")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getXmlTimeSheet(@FormParam("fromDate") String fromDate, @FormParam("toDate") String toDate, @FormParam("filter") String filter)
        throws ParseException, RequiredComponentNotFoundException, ExecutionException, InterruptedException {

        return Response.ok(timesheetGenerator.buildXMLAsync(fromDate, toDate, filter).get())
            .build();

    }

    @POST
    @Path("registerTimesheetExport")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response registerTimesheetExport(@FormParam("fromDate") String fromDate, @FormParam("toDate") String toDate, @FormParam("filter") String filter)
        throws ParseException, RequiredComponentNotFoundException, IOException {

        JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();

        timesheetGenerator.generateXmlAndStore(fromDate, toDate, filter, jiraAuthenticationContext.getUser());

        return Response.ok().build();
    }
}
