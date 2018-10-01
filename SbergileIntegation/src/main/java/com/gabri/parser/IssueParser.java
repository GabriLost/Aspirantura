package com.gabri.parser;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.history.ChangeItemBean;
import com.atlassian.jira.project.version.Version;
import java.io.Serializable;

public class IssueParser implements Serializable {

    public SerializedIssue parse(Issue issue, String cong_elem_field_id, String bug_stage_field_id) {
        SerializedIssue i = new SerializedIssue();
        i.issue_id = issue.getId();
        i.issue_key = issue.getKey();
        i.issue_summary = issue.getSummary();
        i.issue_description = issue.getDescription();
        i.issue_last_update = issue.getUpdated();
        i.issue_type = issue.getIssueType() == null ? "None" : issue.getIssueType().getName();
        i.issue_status = issue.getStatus() == null ? "None" : issue.getStatus().getName();
        i.issue_priority = issue.getPriority() == null ? "None" : issue.getPriority().getName();
        i.project_id = issue.getProjectId();
        i.project_key = issue.getProjectObject() == null ? "None" : issue.getProjectObject().getKey();
        i.project_name = issue.getProjectObject() == null ? "None" : issue.getProjectObject().getName();

        for (Version version : issue.getFixVersions()) {
            if (version.getName() != null && version.getId() != null) {
                if (i.version_id == null) {
                    i.version_id = version.getId().toString();
                    i.version_name = version.getName();
                } else {
                    i.version_id += "#@#" + version.getId();
                    i.version_name += "#@#" + version.getName();
                }
            }
        }

        i.issue_last_update = issue.getUpdated();

        // История
        ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager();
        for (ChangeHistory changeHistory : changeHistoryManager.getChangeHistories(issue)) {
            for (ChangeItemBean b : changeHistory.getChangeItemBeans()) {
                if (b.getField().equalsIgnoreCase("Fix Version")||
                    b.getField().equalsIgnoreCase("Fix Version/s")||
                    b.getField().equalsIgnoreCase("КЭ"))
                {
                    i.last_updated_by = changeHistory.getAuthorDisplayName();
                    i.last_updated_by_date = b.getCreated();
                }
            }
        }
        //кастомные филды
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        for (CustomField cf : customFieldManager.getCustomFieldObjects(issue)) {
            if (cf.getId().equalsIgnoreCase(cong_elem_field_id)) {
                i.issue_conf_elem_id = cf.getValue(issue) == null ? "" : cf.getValue(issue).toString();
            } else if (cf.getId().equalsIgnoreCase(bug_stage_field_id)) {
                i.issue_bug_stage = cf.getValue(issue) == null ? "" : cf.getValue(issue).toString();
            }
        }

        i.last_updated_by = issue.getReporter() == null ? "Anonymous" : issue.getReporter().getDisplayName();
        i.last_updated_by_date = issue.getCreated();
        return i;
    }
}