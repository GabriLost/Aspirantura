package ru.sbertech.atlas.jira.cupintegration.in.service;

import ru.sbertech.atlas.jira.cupintegration.exception.ValidateException;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportSettingsUpdateException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;

public interface ImportSettingService {
    /**
     * Return ImportSettings object for import issues from Cup to Jira
     *
     * @return {@link ImportSettings}
     */
    ImportSettings getImportSetting();

    /**
     * Create or update setting for import issues from Cup to Jira
     *
     * @param importSettings
     * @throws IllegalArgumentException where ImportSettings is null
     * @throws ValidateException where one or more fields are inappropriate
     */
    void updateOrCreateImportSetting(ImportSettings importSettings) throws ImportSettingsUpdateException;
}
