package ru.sbertech.atlas.jira.cupintegration.in.issue;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.user.ApplicationUser;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;

public class UpdateStrategy extends AbstractImportStrategy {

    private final IssueService issueService;

    public UpdateStrategy(IssueService issueService) {
        this.issueService = issueService;
    }

    @Override
    public ImportResult importIssue(ApplicationUser user, IssueInputParameters issueInputParameters, Long issueId) {
        IssueService.UpdateValidationResult updateValidationResult = issueService.validateUpdate(user, issueId, issueInputParameters);
        if (!updateValidationResult.isValid()) {
            return createErrorResult(updateValidationResult.getErrorCollection().toString());
        }
        LOG.debug("Updating issue with params " + updateValidationResult.getFieldValuesHolder().toString());
        IssueService.IssueResult issueResult = issueService.update(user, updateValidationResult);
        if (!issueResult.isValid()) {
            return createErrorResult(issueResult.getErrorCollection().toString());
        }
        return createSuccessResult(ResultState.UPDATED, issueResult.getIssue().getKey());
    }
}
