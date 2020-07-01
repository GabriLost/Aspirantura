package com.gabri;

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
import com.gabri.impl.LastRunManager;
import com.gabri.parser.IssueParser;
import com.gabri.parser.SerializedIssue;
import com.google.gson.Gson;
import com.opensymphony.module.propertyset.PropertySet;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UploadService extends AbstractService{
    private String jqlQuery, user, path, date = null;
    private String cong_elem_field_id;
    private String bug_stage_field_id;
    private String LAST_RUN_KEY = "last.run";
    private String SIMPLE_DATE_FORMAT_FILE = "yyyyMMdd_HHmmss";
    private String SIMPLE_DATE_FORMAT_JIRA = "yyyy/MM/dd HH:mm";
    private final LastRunManager lastRunManager;

    public UploadService(){
        this.lastRunManager = (LastRunManager) ComponentAccessor
                .getOSGiComponentInstanceOfType(LastRunManager.class);
    }
    @Override
    public void init(PropertySet props) throws ObjectConfigurationException {
        super.init(props);

        if (hasProperty("JQL Query"))      jqlQuery = getProperty("JQL Query");
        if (hasProperty("User"))           user = getProperty("User");
        if (hasProperty("Output folder"))  path = getProperty("Output folder");
        if (hasProperty("CF_CONF_ELEM"))   cong_elem_field_id = getProperty("CF_CONF_ELEM");
        if (hasProperty("CF_BUG_STAGE"))   bug_stage_field_id = getProperty("CF_BUG_STAGE");
    }
    public void run() {
        log.info("_"+ LAST_RUN_KEY + " " + lastRunManager.getValue(LAST_RUN_KEY));
//        System.out.println("_JQL_"+jqlQuery);
//        System.out.println("_USER_"+user);
//        System.out.println("_PATH_"+path);
//        System.out.println("_CE_"+cong_elem_field_id);
//        System.out.println("_BS_"+bug_stage_field_id);

        Date startDate = new Date();
    	date = new SimpleDateFormat(SIMPLE_DATE_FORMAT_FILE).format(startDate);
        List<Issue> issues = parseJQL(jqlQuery);
        ArrayList<SerializedIssue> sIssues = new ArrayList<SerializedIssue>();
        if (jqlQuery != null && path != null && user != null && issues != null)
        {
        	for (Issue issue : issues)
        	    sIssues.add((new IssueParser().parse(issue, cong_elem_field_id, bug_stage_field_id)));

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
                lastRunManager.setValue(LAST_RUN_KEY, new SimpleDateFormat(SIMPLE_DATE_FORMAT_JIRA).format(startDate));
        	}
        }
        else log.warn("Something is null!");
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
        final SearchService.ParseResult parseResult = searchService.parseQuery(applicationUser, JQL);

        if (parseResult.isValid()) {
            try
            {
                final SearchResults results = searchService.search(applicationUser, parseResult.getQuery(), PagerFilter.getUnlimitedFilter());
                return results.getIssues();

            }
            catch (SearchException e)
            {
                log.error("Error running search", e);
            }
        } else
            {
                log.warn("Error parsing jqlQuery: " + parseResult.getErrors());
            }
        return null;
    }

    public ObjectConfiguration getObjectConfiguration() throws ObjectConfigurationException {
        return getObjectConfiguration("UploadService",
                "UploadService.xml", null);
    }
}
