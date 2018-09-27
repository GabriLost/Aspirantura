package ru.sbertech.atlas.jira.cupintegration.in.validator;

import com.atlassian.jira.component.ComponentAccessor;
import org.apache.commons.lang.StringUtils;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;

/**
 * Created by Sedelnikov FM on 28/01/2016.
 */
public class ImportSettingsChecker {

    public ImportSettingsChecker checkCustomField(String fieldId) throws Exception {

        if (ComponentAccessor.getCustomFieldManager().getCustomFieldObject(Long.parseLong(fieldId)) == null) {
            throw new Exception("Incorrect import settings.");
        }
        return this;
    }

    public ImportSettingsChecker checkUserName(String userName) throws Exception {
        if (ComponentAccessor.getUserManager().getUserByName(userName) == null) {
            throw new Exception("Incorrect import settings. Username is unknown or null");
        }
        return this;
    }

    public ImportSettingsChecker checkReleaseMapping(ReleaseMapping releaseMapping) throws Exception {
        if (releaseMapping == null) {
            throw new Exception("Release mapping is null.");
        }
        if (StringUtils.isEmpty(releaseMapping.getPpmReleaseId()) || StringUtils.isEmpty(releaseMapping.getPpmReleaseAreaPs())
            || StringUtils.isEmpty(releaseMapping.getPpmReleaseName()) || StringUtils.isEmpty(releaseMapping.getPpmReleaseStartDate())
            || StringUtils.isEmpty(releaseMapping.getPpmReleaseFinishDate()) || StringUtils.isEmpty(releaseMapping.getPpmReleaseStatus())) {
            throw new Exception("Incorrect release mapping.");
        }
        return this;
    }
}
