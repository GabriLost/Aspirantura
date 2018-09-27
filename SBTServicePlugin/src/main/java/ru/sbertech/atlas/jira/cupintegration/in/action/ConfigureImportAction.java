package ru.sbertech.atlas.jira.cupintegration.in.action;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.in.service.ImportSettingService;
import ru.sbertech.atlas.jira.cupintegration.in.service.ReleaseMappingService;

public class ConfigureImportAction extends JiraWebActionSupport {

    private final ImportSettings importSettings;
    private final ReleaseMapping releaseMapping;

    public ConfigureImportAction(ImportSettingService importSettingService, ReleaseMappingService releaseMappingService) {
        importSettings = importSettingService.getImportSetting();
        releaseMapping = releaseMappingService.getReleaseMapping();
    }

    public ImportSettings getImportSettings() {
        return importSettings;
    }

    public ReleaseMapping getReleaseMapping() {
        return releaseMapping;
    }
}
