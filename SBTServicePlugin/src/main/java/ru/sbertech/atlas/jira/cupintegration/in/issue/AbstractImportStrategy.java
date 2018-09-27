package ru.sbertech.atlas.jira.cupintegration.in.issue;

import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.user.ApplicationUser;
import org.apache.log4j.Logger;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;

public abstract class AbstractImportStrategy {

    protected static final Logger LOG = Logger.getLogger(CreateStrategy.class);

    public abstract ImportResult importIssue(ApplicationUser user, IssueInputParameters issueInputParameters, Long issueId);

    protected ImportResult createErrorResult(String failReasons) {
        LOG.error("Can't import issue due to such reasons:" + failReasons);
        return new ImportResult(ResultType.ISSUE, ResultState.ERROR, failReasons);
    }

    protected ImportResult createSuccessResult(ResultState resultState, String issueKey) {
        LOG.debug(String.format("Issue with key: %s processed successfuly", issueKey));
        return new ImportResult(ResultType.ISSUE, resultState, issueKey);
    }
}
