package ru.gabri;

import java.io.Serializable;

class SerializedIssue implements Serializable {
    public Long issue_id;
    public String issue_key;
    public String issue_type;
    public String issue_summary;
    public String issue_description;
    public String issue_status;
    public String issue_priority;
    public String issue_conf_elem_id;
    public String version_id = null;
    public String version_name = null;
    public Long project_id;
    public String project_key;
    public String project_name;
    public String last_updated_by;
    public java.sql.Timestamp last_updated_by_date;
    public java.sql.Timestamp issue_last_update;
    public String issue_source = "";
    public String issue_bug_stage;
}

//    SerializedIssue(Issue issue, String cong_elem_field_id, String bug_stage_field_id) {
//        this.issue_id = issue.getId();
//        this.issue_key = issue.getKey();
//        this.issue_summary = issue.getSummary();
//        this.issue_description  = issue.getDescription();
//        this.issue_last_update  = issue.getUpdated();
//        this.issue_type         = issue.getIssueType()  == null ? "None" : issue.getIssueType().getName();
//        this.issue_status       = issue.getStatus()     == null ? "None" : issue.getStatus().getName();
//        this.issue_priority     = issue.getPriority()   == null ? "None" : issue.getPriority().getName();
//        this.project_id         = issue.getProjectId();
//        this.project_key        = issue.getProjectObject() == null ? "None" : issue.getProjectObject().getKey();
//        this.project_name       = issue.getProjectObject() == null ? "None" : issue.getProjectObject().getName();
//
//        for (Version version : issue.getFixVersions()) {
//            if (version.getName() != null && version.getId()!= null) {
//                if (version_id == null) {
//                    version_id = version.getId().toString();
//                    version_name = version.getName();
//                } else {
//                    version_id  += "#@#" + version.getId();
//                    version_name +=  "#@#" + version.getName();
//                }
//            }
//        }
//
//        this.issue_last_update  = issue.getUpdated();
//        //TODO
//        this.issue_source       = issue.getEnvironment();
//
//        //        getSelf().getScheme()+"://"+issue.getSelf().getHost() + issue.getSelf().getPath().substring(0,issue.getSelf().getPath().substring(1).indexOf("/rest")+1);
//        // История
//        ChangeHistoryManager changeHistoryManager = ComponentAccessor.getChangeHistoryManager();
//        for (ChangeHistory changeHistory : changeHistoryManager.getChangeHistories(issue)) {
//            System.out.println(issue.getKey());
//            System.out.println(changeHistory.getAuthorDisplayName());
//            System.out.println(changeHistory.getChangeItemBeans().get(0).getField());
//            System.out.println(changeHistory.getChangeItemBeans().get(0).getFrom());
//            System.out.println(changeHistory.getChangeItemBeans().get(0).getTo());
//            System.out.println(changeHistory.getChangeItemBeans().get(0).getFromString());
//            System.out.println(changeHistory.getChangeItemBeans().get(0).getToString());
//            System.out.println(changeHistory.getChangeItemBeans().get(0).getCreated());
//            for (ChangeItemBean b : changeHistory.getChangeItemBeans()){
//                System.out.println("loop bean" + b.getField());
//            }
//
////
////            for (ChangeItemBean bean : changeHistory.getChangeItemBeans()){
////                System.out.println(changeHistory.);
////                if (bean.getField().equals("Fix Version")){
////                    if (last_updated_by_date.before(bean.getCreated())
////                            || last_updated_by_date.equals(bean.getCreated())) {
////                        last_updated_by = bean.getAuthor() == null ? "Anonymous" : objChangeLog.getAuthor().getDisplayName();
//////                        log_last_update_date = objChangeLog.getCreated();
////                }
////                System.out.println(bean.getField());
////                System.out.println(bean.getField());
////
////          }
//        }
//        //кастомные филды
//        CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();
//        for (CustomField cf :customFieldManager.getCustomFieldObjects(issue)){
//            if(cf.getId().equalsIgnoreCase(cong_elem_field_id)) {
//                System.out.println(" e"+ cf.getId() + " " + cf.getUntranslatedName() + " " + cf.getValue(issue));
//                i
//            }
//            else
//            if(cf.getId().equalsIgnoreCase(bug_stage_field_id)){
//                System.out.println(" s"+ cf.getId()+" "+ cf.getUntranslatedName() +" " + cf.getValue(issue));
//            }
//            else
//                System.out.println("  " + cf.getId() + " " + cf.getUntranslatedName() + " " + cf.getValue(issue));
//
//        }
//
//        this.last_updated_by          = issue.getReporter() == null ? "Anonymous" : issue.getReporter().getDisplayName();
//        this.last_updated_by_date     = issue.getCreated();
//    }
//}

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