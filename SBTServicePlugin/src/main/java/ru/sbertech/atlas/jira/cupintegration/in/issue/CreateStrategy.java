package ru.sbertech.atlas.jira.cupintegration.in.issue;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.user.ApplicationUser;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;

public class CreateStrategy extends AbstractImportStrategy {

    private final IssueService issueService;

    public CreateStrategy(IssueService issueService) {
        this.issueService = issueService;
    }

    @Override
    public ImportResult importIssue(ApplicationUser user, IssueInputParameters issueInputParameters, Long issueId) {
        IssueService.CreateValidationResult createValidationResult = issueService.validateCreate(user, issueInputParameters);
        if (!createValidationResult.isValid()) {
            return createErrorResult(createValidationResult.getErrorCollection().toString());
        }
        LOG.debug("Start creating issue with params " + createValidationResult.getFieldValuesHolder().toString());
        IssueService.IssueResult issueResult = issueService.create(user, createValidationResult);
        if (!issueResult.isValid()) {
            return createErrorResult(issueResult.getErrorCollection().toString());
        }
        return createSuccessResult(ResultState.CREATED, issueResult.getIssue().getKey());
    }
}
