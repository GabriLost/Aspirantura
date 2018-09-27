package ru.sbertech.atlas.jira.cupintegration.issuerendering.processor;

import com.atlassian.jira.issue.*;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbertech.atlas.jira.cupintegration.in.service.ImportSettingService;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.model.EpicSyncResult;
import ru.sbertech.atlas.jira.cupintegration.issuerendering.service.EpicSyncSearchService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EpicIssueSyncProcessorImpl implements EpicIssueSyncProcessor {
    private final static Logger log = LoggerFactory.getLogger(EpicIssueSyncProcessorImpl.class);

    private final CustomFieldManager customFieldManager;
    private final ImportSettingService importSettingService;
    private final EpicSyncSearchService epicSyncSearchService;
    private final IssueManager issueManager;

    public EpicIssueSyncProcessorImpl(CustomFieldManager customFieldManager, ImportSettingService importSettingService, EpicSyncSearchService epicSyncSearchService,
        IssueManager issueManager) {
        this.customFieldManager = customFieldManager;
        this.importSettingService = importSettingService;
        this.epicSyncSearchService = epicSyncSearchService;
        this.issueManager = issueManager;
    }

    @Override
    public Set<EpicSyncResult> updateIssues(String idEpic, String[] selectedIssueIds) throws SearchException {
        List<String> selectedIds = Arrays.asList(selectedIssueIds);
        String fieldKrpId = importSettingService.getImportSetting().getCupKrpIdField();
        MutableIssue epic = issueManager.getIssueObject(idEpic);
        CustomField cfKrpId = customFieldManager.getCustomFieldObject(Long.parseLong(fieldKrpId));
        Set<EpicSyncResult> result = new HashSet<>();
        String krpValue = cfKrpId.getValueFromIssue(epic);
        Set<Issue> issues = epicSyncSearchService.getIssueByQuery(epicSyncSearchService.buildQueryByEpicIdTimeSpentEmpty(epic.getKey()));
        for (Issue is : issues) {
            if (!selectedIds.contains(is.getKey())) {
                continue;
            }
            log.debug("Try to sync issue with key: " + is.getKey());
            try {
                String oldValue = (String) is.getCustomFieldValue(cfKrpId);
                cfKrpId.updateValue(null, is, new ModifiedValue(oldValue, krpValue), new DefaultIssueChangeHolder());
                result.add(new EpicSyncResult(is, oldValue, krpValue));
            } catch (Exception e) {
                if (is == null) {
                    /** Logger shouldn't have any exceptions */
                    log.error("Can't update issue: issue is null ", e);
                } else {
                    log.error("Can't update issue with key: " + is.getKey(), e);
                }
            }
        }
        return result;
    }
}
