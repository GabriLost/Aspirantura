package ru.sbertech.atlas.jira.cupintegration.in.validator;

import com.atlassian.jira.bc.project.ProjectService;
import com.atlassian.jira.bc.project.version.VersionService;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SBT-Dmitriyev-VV on 01.09.2016.
 */
public class ImportReleaseValidator {

    public ImportSettings validateImportSettings(ImportSettings importSettings) throws ImportException {
        if (importSettings == null) {
            throw new ImportException("Import settings must not be null.");
        }
        return importSettings;
    }

    public ApplicationUser validateUser(ApplicationUser user, String userName) throws ImportException {
        if (user == null) {
            throw new ImportException("User with name \"" + userName + "\" not found.");
        }
        return user;
    }

    public Project validateProject(ProjectService.GetProjectResult project) throws ImportException {
        if (!project.isValid()) {
            throw new ImportException(project.getErrorCollection().toString());
        }
        return project.getProject();
    }

    public List<Version> validateReleases(VersionService.VersionsResult versions, String ppmReleaseId, String projectName) throws ImportException {
        List<Version> results = new ArrayList<>();
        if (!versions.isValid()) {
            throw new ImportException(versions.getErrorCollection().toString());
        }
        for (Version version : versions.getVersions()) {
            if (version.getName() != null && version.getName().contains(ppmReleaseId + "_")) {
                results.add(version);
            }
        }
        if (results.size() > 1) {
            throw new ImportException("More then one releases with ppm_release_id \"" + ppmReleaseId + "\" in project \"" + projectName + "\"");
        }
        return results;
    }
}
