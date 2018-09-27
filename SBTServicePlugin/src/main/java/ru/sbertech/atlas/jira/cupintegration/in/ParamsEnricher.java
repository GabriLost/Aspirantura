package ru.sbertech.atlas.jira.cupintegration.in;

import com.atlassian.jira.bc.project.version.VersionBuilder;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.in.utils.DateUtils;

import java.util.Map;

/**
 * @author Dmitriy Klabukov
 * @date 11/01/2016.
 */
public class ParamsEnricher {

    private static final String FIELD_EPIC_NAME = "Epic Name";
    private static final String DEFAULT_SUMMARY = "PPM Issue";

    private final ProjectManager projectManager;
    private final CustomFieldManager customFieldManager;

    private ParamsEnricher(ProjectManager projectManager, CustomFieldManager customFieldManager) {
        this.projectManager = projectManager;
        this.customFieldManager = customFieldManager;
    }

    public void enrichIssueCustomFields(Map<String, String> issueParams, IssueInputParameters issueInputParameters) {
        CustomField epicName = customFieldManager.getCustomFieldObjectByName(FIELD_EPIC_NAME);
        if (epicName != null) {
            issueInputParameters.addCustomFieldValue(epicName.getId(), DEFAULT_SUMMARY);
        }

        for (Map.Entry<String, String> e : issueParams.entrySet()) {
            issueInputParameters.addCustomFieldValue(e.getKey(), (String) e.getValue());
        }
    }

    public void enrichIssueDefaultFields(Map<String, String> issueParams, IssueInputParameters issueInputParameters, ApplicationUser user, String issueType) {
        issueInputParameters.setSummary(DEFAULT_SUMMARY);
        issueInputParameters.setIssueTypeId(issueType);
        if (user != null) {
            String userKey = user.getKey();
            issueInputParameters.setReporterId(userKey);
            issueInputParameters.setAssigneeId(userKey);
        }

        for (Map.Entry<String, String> entry : issueParams.entrySet()) {
            switch (entry.getKey()) {
                case IssueFieldConstants.PROJECT:
                    Project project = projectManager.getProjectByCurrentKeyIgnoreCase(entry.getValue());
                    issueInputParameters.setProjectId(project == null ? null : project.getId());
                    break;

                case IssueFieldConstants.SUMMARY:
                    issueInputParameters.setSummary(entry.getValue());
                    break;

                case IssueFieldConstants.DUE_DATE:
                    issueInputParameters.setDueDate(entry.getValue());
                    break;

                case IssueFieldConstants.RESOLUTION_DATE:
                    issueInputParameters.setResolutionDate(entry.getValue());
                    break;

                case IssueFieldConstants.TIME_ORIGINAL_ESTIMATE:
                    issueInputParameters.setOriginalEstimate(entry.getValue());
                    break;
            }
        }

    }

    public void enrichReleaseFields(Long projectId, ReleaseMapping releaseMapping, Map<String, String> releaseParams, VersionBuilder releaseBuilder)
        throws ImportException {
        releaseBuilder.projectId(projectId).description("[Release from Project portfolio management]");
        if (releaseParams.get(releaseMapping.getPpmReleaseName()) != null) {
            releaseBuilder.name(releaseParams.get(releaseMapping.getPpmReleaseId()) + "_" + releaseParams.get(releaseMapping.getPpmReleaseName()));
        }
        if (releaseParams.get(releaseMapping.getPpmReleaseStartDate()) != null) {
            releaseBuilder.startDate(DateUtils.parseDate(releaseParams.get(releaseMapping.getPpmReleaseStartDate())));
        }
        if (releaseParams.get(releaseMapping.getPpmReleaseFinishDate()) != null) {
            releaseBuilder.releaseDate(DateUtils.parseDate(releaseParams.get(releaseMapping.getPpmReleaseFinishDate())));
        }
    }
}
