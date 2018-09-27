JIRA.Dialogs.deleteIssue = new JIRA.FormDialog({
    id: "sync-issue-dialog",
    trigger: "a.sync-issue-style",
    ajaxOptions: JIRA.Dialogs.getDefaultAjaxOptions,
    onSuccessfulSubmit : JIRA.Dialogs.storeCurrentIssueIdOnSucessfulSubmit,
    issueMsg : 'thanks_issue_synced',
    delayShowUntil: JIRA.Dialogs.BeforeShowIssueDialogHandler.execute,
    isIssueDialog: true,
    widthClass: "large"
});
