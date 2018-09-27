package ru.sbertech.atlas.jira.cupintegration.issuerendering.model;


import com.atlassian.jira.issue.Issue;

public class EpicSyncResult {
    private final Issue updatedIssue;
    private final String OldValue;
    private final String newValue;

    public EpicSyncResult(Issue updatedIssue, String oldValue, String newValue) {
        this.updatedIssue = updatedIssue;
        OldValue = oldValue;
        this.newValue = newValue;
    }

    public Issue getUpdatedIssue() {
        return updatedIssue;
    }

    public String getOldValue() {
        return OldValue;
    }

    public String getNewValue() {
        return newValue;
    }
}
