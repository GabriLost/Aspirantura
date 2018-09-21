package com.bdas;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.google.gson.*;
import com.atlassian.configurable.ObjectConfiguration;
import com.atlassian.configurable.ObjectConfigurationException;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.service.AbstractService;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.opensymphony.module.propertyset.PropertySet;

import java.util.List;

public class PingService extends AbstractService{
    private String jqlQuery, user, path, date = null;
    private Date currentDate;
    
    @Override
    public void init(PropertySet props) throws ObjectConfigurationException {
    	
        super.init(props);
        if (hasProperty("JQL Query"))   jqlQuery = getProperty("JQL Query");
        if (hasProperty("User"))            user = getProperty("User");
        if (hasProperty("Output folder"))   path = getProperty("Output folder");

    }

    public void run() {
        currentDate = new Date();
    	DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
    	date = df.format(currentDate);
        List<Issue> issues = parseJQL(jqlQuery);
        ArrayList<SerializedIssue> sIssues = new ArrayList<SerializedIssue>();
        if (jqlQuery != null && path != null && user != null && issues != null)
        {
        	for (Issue issue : issues)
        	    sIssues.add((new SerializedIssue(issue)));

        	if (!sIssues.isEmpty()){
                Gson gson = new Gson();
        	    String json = gson.toJson(sIssues);
                try(FileWriter writer = new FileWriter(path+"gson_"+date+".txt", true)){
                    writer.write(json);
                    writer.close();
                }
                catch(IOException ex){
                    System.out.println(ex.getMessage());
                }
        	}
        }
        else log.warn("Something is null, Gabri!");
    }

    private ApplicationUser setAppUser(String user) {
        ApplicationUser applicationUser = ComponentAccessor.getUserManager().getUserByKey(user);
        JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();
        jiraAuthenticationContext.setLoggedInUser(applicationUser);
        return jiraAuthenticationContext.getLoggedInUser();
    }

    private List<Issue> parseJQL(String JQL) {
        ApplicationUser applicationUser = setAppUser(user);
        SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);
        final SearchService.ParseResult parseResult = searchService.parseQuery(applicationUser, jqlQuery);

        if (parseResult.isValid()) {
            try
            {
                final SearchResults results = searchService.search(applicationUser, parseResult.getQuery(), PagerFilter.getUnlimitedFilter());
                return results.getIssues();

            }
            catch (SearchException e)
            {
                log.error("Error running search", e);
                System.out.println("Error running search");
            }
        } else
            {
                log.warn("Error parsing jqlQuery: " + parseResult.getErrors());
                System.out.println("Error parsing jqlQuery: " + parseResult.getErrors());

            }
        return null;
    }

    public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException {
        return getObjectConfiguration("PingService",
                "PingService.xml", null);
    }
}
