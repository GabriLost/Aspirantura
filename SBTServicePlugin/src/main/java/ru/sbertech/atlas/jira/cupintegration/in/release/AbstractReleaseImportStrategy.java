package ru.sbertech.atlas.jira.cupintegration.in.release;

import com.atlassian.jira.bc.project.version.VersionBuilder;
import com.atlassian.jira.bc.project.version.VersionService;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;

/**
 * @author Dmitriev Vladimir
 */
public abstract class AbstractReleaseImportStrategy {

    protected static final Logger LOG = Logger.getLogger(ReleaseCreateStrategy.class);

    protected final VersionService versionService;

    protected AbstractReleaseImportStrategy(VersionService versionService) {
        this.versionService = versionService;
    }

    public abstract ImportResult importRelease(ApplicationUser user, VersionBuilder release, String status, String projectName) throws Exception;

    protected ImportResult updateReleaseStatus(ApplicationUser user, Version release, String status, ResultState state, String projectKey) throws Exception {
        if (!StringUtils.isEmpty(status) && !release.isReleased() && status.equals("closed")) {
            if (release.getReleaseDate() == null) {
                return createErrorResult("Update release status error: release date must not be null.");
            }
            VersionService.ReleaseVersionValidationResult validationResult = versionService.validateReleaseVersion(user, release, release.getReleaseDate());
            if (!validationResult.isValid()) {
                return createErrorResult(validationResult.getErrorCollection().toString());
            }
            versionService.releaseVersion(validationResult);
        }
        return createSuccessResult(state, release, projectKey);
    }

    protected ImportResult createErrorResult(String failReasons) {
        LOG.error("Can't import release due to such reasons:" + failReasons);
        return new ImportResult(ResultType.RELEASE, ResultState.ERROR, failReasons);
    }

    private ImportResult createSuccessResult(ResultState resultState, Version release, String projectKey) {
        LOG.debug(String.format("Release with name: %s processed successfuly", release.getName()));
        return new ImportResult(ResultType.RELEASE, resultState, release.getName(), release.getId(), projectKey);
    }
}
