package com.bdas;

import java.io.Serializable;
import java.sql.Timestamp;

import com.atlassian.jira.issue.Issue;
class SerializedIssue implements Serializable {
    Long   id;
    String key;
    String summary;
    String description;
    String status;
    Timestamp updated;

    SerializedIssue(Issue issue){
        this.id = issue.getId();
        this.key = issue.getKey();
        this.summary = issue.getSummary();
        this.description = issue.getDescription();
        this.updated = issue.getUpdated();
        this.status = issue.getStatus().getName();
        //686773
    }
}