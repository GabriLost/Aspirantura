package ru.sbertech.atlas.jira.cupintegration.issuerendering.webaction;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.usercompatibility.UserCompatibilityHelper;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.model.EpicSyncResult;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.processor.EpicIssueSyncProcessor;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.service.EpicSyncSearchService;

import java.util.Set;

public class EpicSyncAction extends JiraWebActionSupport {

    private final static Logger log = LoggerFactory.getLogger(EpicSyncAction.class);

    private final EpicIssueSyncProcessor epicIssueSyncProcessor;
    private final EpicSyncSearchService epicSyncSearchService;
    private String epicId;
    private Set<EpicSyncResult> epicSyncResults;
    private Set<Issue> issues;
    private String[] selectedIssueIds;

    public EpicSyncAction(EpicIssueSyncProcessor epicIssueSyncProcessor, EpicSyncSearchService epicSyncSearchService) {
        this.epicIssueSyncProcessor = epicIssueSyncProcessor;
        this.epicSyncSearchService = epicSyncSearchService;
    }

    @Override
    public String doDefault() throws Exception {
        Query query = epicSyncSearchService.buildQueryByEpicId(epicId);
        issues = epicSyncSearchService.getIssueByQuery(query);
        if (selectedIssueIds == null) {
            return INPUT;
        }
        try {
            User loggedInUser = UserCompatibilityHelper.convertUserObject(ComponentAccessor.getJiraAuthenticationContext().getUser()).getUser();
            if(loggedInUser == null){
                log.info("Try to update issues with epicLink: " + epicId + " for anonymous user: " + loggedInUser);
            }else {
                log.info("Try to update issues with epicLink: " + epicId + " username: " + loggedInUser.getName());
            }
            epicSyncResults = epicIssueSyncProcessor.updateIssues(epicId, selectedIssueIds);
            return SUCCESS;
        } catch (Exception e) {
            log.error("Can't sync epic with id: " + epicId, e);
            return ERROR;
        }
    }

    public String getEpicId() {
        return epicId;
    }

    public void setEpicId(String epicId) {
        this.epicId = epicId;
    }

    public Set<EpicSyncResult> getEpicSyncResults() {
        return epicSyncResults;
    }

    public Set<Issue> getIssues() {
        return issues;
    }

    public String[] getSelectedIssueIds() {
        return selectedIssueIds;
    }

    public void setSelectedIssueIds(String[] selectedIssueIds) {
        this.selectedIssueIds = selectedIssueIds;
    }
}
