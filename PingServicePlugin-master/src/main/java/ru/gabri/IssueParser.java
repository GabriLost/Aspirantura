package ru.gabri;

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

    SerializedIssue parse(Issue issue, String cong_elem_field_id, String bug_stage_field_id) {
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

        //i.issue_source       = issue.getEnvironment()
        //getSelf().getScheme()+"://"+issue.getSelf().getHost() + issue.getSelf().getPath().substring(0,issue.getSelf().getPath().substring(1).indexOf("/rest")+1);
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
//                System.out.println(issue.getKey());
//                System.out.println(changeHistory.getAuthorDisplayName());
//                System.out.println(b.getField());
//                System.out.println(b.getFrom());
//                System.out.println(b.getTo());
//                System.out.println(b.getFromString());
//                System.out.println(b.getToString());
//                System.out.println(b.getCreated());
            }

//
//            for (ChangeItemBean bean : changeHistory.getChangeItemBeans()){
//                System.out.println(changeHistory.);
//                if (bean.getField().equals("Fix Version")){
//                    if (last_updated_by_date.before(bean.getCreated())
//                            || last_updated_by_date.equals(bean.getCreated())) {
//                        last_updated_by = bean.getAuthor() == null ? "Anonymous" : objChangeLog.getAuthor().getDisplayName();
////                        log_last_update_date = objChangeLog.getCreated();
//                }
//                System.out.println(bean.getField());
//                System.out.println(bean.getField());
//
//          }
        }
        //кастомные филды
        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
        for (CustomField cf : customFieldManager.getCustomFieldObjects(issue)) {
            if (cf.getId().equalsIgnoreCase(cong_elem_field_id)) {
                i.issue_conf_elem_id = cf.getValue(issue) == null ? "" : cf.getValue(issue).toString();
            } else if (cf.getId().equalsIgnoreCase(bug_stage_field_id)) {
                i.issue_bug_stage = cf.getValue(issue) == null ? "" : cf.getValue(issue).toString();
            }
//            else
//                System.out.println("  " + cf.getId() + " " + cf.getUntranslatedName() + " " + cf.getValue(issue));

        }

        i.last_updated_by = issue.getReporter() == null ? "Anonymous" : issue.getReporter().getDisplayName();
        i.last_updated_by_date = issue.getCreated();

        return i;
    }
}

//
//    //кем и когда последний раз изменялось поле "Fix Version" или "Implement in" или "КЭ"
//            if (issue.getChangelog() != null){
//        for (ChangelogGroup objChangeLog : issue.getChangelog()) {
//            for (ChangelogItem item : objChangeLog.getItems()) {
////                        if (!item.getField().equals("RemoteIssueLink"))
////                            issue_last_update = objChangeLog.getCreated();
//                if (item.getField().equals("Fix Version")
//                        || (!implemInFieldName.isEmpty() && item.getField().equals(implemInFieldName))
//                        || (!confElemFieldName.isEmpty() && item.getField().equals(confElemFieldName))
//                        ) {
//                    if (log_last_update_date.isBefore(objChangeLog.getCreated())
//                            || log_last_update_date.isEqual(objChangeLog.getCreated())) {
//                        last_updated_by = objChangeLog.getAuthor() == null ? "Anonymous" : objChangeLog.getAuthor().getDisplayName();
//                        log_last_update_date = objChangeLog.getCreated();
//                    }
//                }
//            }
//        }
//    }
//    last_updated_by = last_updated_by + "#@#" + log_last_update_date.toLocalDateTime().toString();
//
//            for (Version versionItem : issue.getFixVersions()) {
//        if (versionItem.getName() != null && versionItem.getId()!= null) {
//            if (version_id == null) {
//                version_id = versionItem.getId().toString();
//                version_name = versionItem.getName();
//            } else {
//                version_id  = version_id + "#@#" + versionItem.getId();
//                version_name = version_name + "#@#" + versionItem.getName();
//            }
//        }
//    }
//            System.out.print(issue.getKey()+", ");
//    processIssueToDB(
//            issue_id.intValue(),
//    issue_key,
//    issue_type,
//    issue_summary,
//    issue_description,
//    issue_status,
//    issue_priority,
//    issue_conf_elem_id,
//    version_id,
//    version_name,
//    project_id,
//    project_key,
//    project_name,
//    last_updated_by,
//    issue_last_update,
//    issue_source,
//    issue_bug_stage);
//}