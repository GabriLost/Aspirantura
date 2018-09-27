package ru.sbertech.atlas.jira.cupintegration.out.service;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import ru.sbertech.atlas.jira.cupintegration.out.model.PluginSettingsHolder;

import java.util.concurrent.locks.ReentrantLock;

public class PluginSettingsService {

    private final PluginSettings pluginSettings;
    private final ReentrantLock lock = new ReentrantLock();

    public PluginSettingsService(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    public PluginSettingsHolder getPluginSettings() {
        lock.lock();
        try {
            return new PluginSettingsHolder((String) pluginSettings.get(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SHARED_FOLDER),
                (String) pluginSettings.get(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_AUTO_EXPORT),
                (String) pluginSettings.get(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SCHEDULE),
                (String) pluginSettings.get(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_FILTER),
                (String) pluginSettings.get(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_FILE_PREFIX),
                (String) pluginSettings.get(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SMB_WEB_SERVICE),
                (String) pluginSettings.get(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SMB_LOGIN),
                (String) pluginSettings.get(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SMB_PASSWORD),
                (String) pluginSettings.get(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SMB_PATH),
                (String) pluginSettings.get(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_RESULT_SERVICE_URL));
        } finally {
            lock.unlock();
        }
    }

    public void setSettings(PluginSettingsHolder settings) {
        lock.lock();
        try {
            pluginSettings.put(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SHARED_FOLDER, settings.sharedFolder);
            pluginSettings.put(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_AUTO_EXPORT, settings.autoExport);
            pluginSettings.put(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SCHEDULE, settings.schedule);
            pluginSettings.put(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_FILTER, settings.filter);
            pluginSettings.put(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_FILE_PREFIX, settings.filePrefix);
            pluginSettings.put(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SMB_WEB_SERVICE, settings.exportSmbWebService);
            pluginSettings.put(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SMB_LOGIN, settings.exportSmbLogin);
            pluginSettings.put(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SMB_PASSWORD, settings.exportSmbPassword);
            pluginSettings.put(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_SMB_PATH, settings.exportPathSmb);
            pluginSettings.put(PluginSettingsHolder.TIMESHEET_EXPORT_SETTINGS_RESULT_SERVICE_URL, settings.resultServiceUrl);
        } finally {
            lock.unlock();
        }
    }
}
