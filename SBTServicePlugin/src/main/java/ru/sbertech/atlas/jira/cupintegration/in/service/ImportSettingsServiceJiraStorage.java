package ru.sbertech.atlas.jira.cupintegration.in.service;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportSettingsUpdateException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.out.model.PluginSettingsHolder;
import ru.sbertech.atlas.jira.cupintegration.validator.ValidationProcessor;

import java.util.concurrent.locks.ReentrantLock;

public class ImportSettingsServiceJiraStorage implements ImportSettingService {
    public static final String IMPORT_SETTINGS_AUTO_EXPORT = PluginSettingsHolder.PLUGIN_PREFIX + "importAutoExport";
    public static final String IMPORT_SETTINGS_FOLDER = PluginSettingsHolder.PLUGIN_PREFIX + "importFolder";
    public static final String IMPORT_SETTINGS_QUARTZ_EXPRESSION = PluginSettingsHolder.PLUGIN_PREFIX + "importQuartzExpression";
    public static final String IMPORT_SETTINGS_CUP_ZNI_ID_FIELD = PluginSettingsHolder.PLUGIN_PREFIX + "cupZniIdField";
    public static final String IMPORT_SETTINGS_CUP_KRP_ID_FIELD = PluginSettingsHolder.PLUGIN_PREFIX + "cupKrpIdField";
    public static final String IMPORT_SETTINGS_USER = PluginSettingsHolder.PLUGIN_PREFIX + "userName";

    private final PluginSettings pluginSettings;
    private final ReentrantLock lock = new ReentrantLock();

    public ImportSettingsServiceJiraStorage(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    @Override
    public ImportSettings getImportSetting() {
        lock.lock();
        try {
            return new ImportSettings((String) pluginSettings.get(IMPORT_SETTINGS_FOLDER), (String) pluginSettings.get(IMPORT_SETTINGS_QUARTZ_EXPRESSION),
                Boolean.parseBoolean((String) pluginSettings.get(IMPORT_SETTINGS_AUTO_EXPORT)), (String) pluginSettings.get(IMPORT_SETTINGS_CUP_ZNI_ID_FIELD),
                (String) pluginSettings.get(IMPORT_SETTINGS_USER), (String) pluginSettings.get(IMPORT_SETTINGS_CUP_KRP_ID_FIELD));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateOrCreateImportSetting(ImportSettings importSettings) throws ImportSettingsUpdateException {
        lock.lock();
        try {
            new ValidationProcessor<ImportSettings>().validateSetting(importSettings);
            pluginSettings.put(IMPORT_SETTINGS_FOLDER, importSettings.importFolder);
            pluginSettings.put(IMPORT_SETTINGS_QUARTZ_EXPRESSION, importSettings.quartzExpression);
            pluginSettings.put(IMPORT_SETTINGS_AUTO_EXPORT, importSettings.autoImportEnabled.toString());
            pluginSettings.put(IMPORT_SETTINGS_CUP_ZNI_ID_FIELD, importSettings.cupZniIdField);
            pluginSettings.put(IMPORT_SETTINGS_USER, importSettings.userName);
            pluginSettings.put(IMPORT_SETTINGS_CUP_KRP_ID_FIELD, importSettings.cupKrpIdField);
        } finally {
            lock.unlock();
        }
    }
}
