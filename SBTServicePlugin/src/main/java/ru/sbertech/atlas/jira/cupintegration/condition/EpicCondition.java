package ru.sbertech.atlas.jira.cupintegration.condition;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.user.ApplicationUser;

import java.util.Map;

public class EpicCondition extends AbstractWebCondition {

    @Override
    public boolean shouldDisplay(ApplicationUser user, JiraHelper jiraHelper) {
        final Map<String, Object> params = jiraHelper.getContextParams();

        final Issue issue = (Issue) params.get("issue");
        if (issue == null) {
            return false;
        }

        if (issue.getIssueTypeObject().getName().equals("Epic")) {
            return true;
        }
        return false;
    }
}
