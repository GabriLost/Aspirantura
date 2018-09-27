package ru.sbertech.atlas.jira.cupintegration.in.release;

import com.atlassian.jira.bc.ServiceOutcome;
import com.atlassian.jira.bc.project.version.VersionBuilder;
import com.atlassian.jira.bc.project.version.VersionService;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;

/**
 * @author Dmitriev Vladimir
 */
public class ReleaseUpdateStrategy extends AbstractReleaseImportStrategy {

    public ReleaseUpdateStrategy(VersionService versionService) {
        super(versionService);
    }

    @Override
    public ImportResult importRelease(ApplicationUser user, VersionBuilder releaseBuilder, String status, String projectKey) throws Exception {
        VersionService.VersionBuilderValidationResult validationResult  = versionService.validateUpdate(user, releaseBuilder);
        if (!validationResult.isValid()) {
            return createErrorResult(validationResult.getErrorCollection().toString());
        }
        ServiceOutcome<Version> result = versionService.update(user, validationResult);
        if (!result.isValid()) {
            return createErrorResult(result.getErrorCollection().toString());
        }
        LOG.debug("Updating release with name " + result.getReturnedValue().getName());
        return updateReleaseStatus(user, result.getReturnedValue(), status, ResultState.UPDATED, projectKey);
    }
}
