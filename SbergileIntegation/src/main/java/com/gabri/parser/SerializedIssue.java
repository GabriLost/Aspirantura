package com.gabri.parser;

import java.io.Serializable;

public class SerializedIssue implements Serializable {
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