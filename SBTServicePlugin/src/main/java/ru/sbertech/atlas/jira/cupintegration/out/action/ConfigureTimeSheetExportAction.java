package ru.sbertech.atlas.jira.cupintegration.out.action;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.sbertech.atlas.jira.cupintegration.out.TimesheetGenerator;
import ru.sbertech.atlas.jira.cupintegration.out.model.PluginSettingsHolder;
import ru.sbertech.atlas.jira.cupintegration.out.service.PluginSettingsService;

public class ConfigureTimeSheetExportAction extends JiraWebActionSupport {

    private transient final PluginSettingsService pluginSettingsService;
    private transient final TimesheetGenerator timesheetGenerator;

    private String sharedFolder;
    private String autoExport;
    private String schedule;
    private String filter;
    private String filePrefix;
    private String exportSmbWebService;
    private String exportSmbLogin;
    private String exportSmbPassword;
    private String exportPathSmb;
    private String resultServiceUrl;

    public ConfigureTimeSheetExportAction(PluginSettingsService pluginSettingsService, TimesheetGenerator timesheetGenerator) {
        this.pluginSettingsService = pluginSettingsService;
        this.timesheetGenerator = timesheetGenerator;

        PluginSettingsHolder pluginSettings = this.pluginSettingsService.getPluginSettings();

        sharedFolder = pluginSettings.sharedFolder;
        schedule = pluginSettings.schedule;
        filter = pluginSettings.filter;
        autoExport = pluginSettings.autoExport;
        filePrefix = pluginSettings.filePrefix;
        exportSmbWebService = pluginSettings.exportSmbWebService;
        exportSmbLogin = pluginSettings.exportSmbLogin;
        exportSmbPassword = pluginSettings.exportSmbPassword;
        exportPathSmb = pluginSettings.exportPathSmb;
        resultServiceUrl = pluginSettings.resultServiceUrl;
    }

    @Override
    public String doDefault() throws Exception {
        PluginSettingsHolder settings =
            new PluginSettingsHolder(sharedFolder, autoExport, schedule, filter, filePrefix, exportSmbWebService, exportSmbLogin, exportSmbPassword, exportPathSmb,
                resultServiceUrl);
        pluginSettingsService.setSettings(settings);
        try {
            timesheetGenerator.reschedulePeriodicExport(settings);
        } catch (Exception e) {
            log.error("Error while creating periodic job [" + e.getMessage() + "]", e);
            addErrorMessage("Error while creating periodic job [" + e.getMessage() + "]", Reason.SERVER_ERROR);
        }

        return INPUT;
    }

    public String getSharedFolder() {
        return sharedFolder;
    }

    public void setSharedFolder(String sharedFolder) {
        this.sharedFolder = sharedFolder;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getAutoExport() {
        return autoExport;
    }

    public void setAutoExport(String autoExport) {
        this.autoExport = autoExport;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getExportSmbWebService() {
        return exportSmbWebService;
    }

    public void setExportSmbWebService(String exportSmbWebService) {
        this.exportSmbWebService = exportSmbWebService;
    }

    public String getExportSmbLogin() {
        return exportSmbLogin;
    }

    public void setExportSmbLogin(String exportSmbLogin) {
        this.exportSmbLogin = exportSmbLogin;
    }

    public String getExportSmbPassword() {
        return exportSmbPassword;
    }

    public void setExportSmbPassword(String exportSmbPassword) {
        this.exportSmbPassword = exportSmbPassword;
    }

    public String getExportPathSmb() {
        return exportPathSmb;
    }

    public void setExportPathSmb(String exportPathSmb) {
        this.exportPathSmb = exportPathSmb;
    }

    public String getResultServiceUrl() {
        return resultServiceUrl;
    }

    public void setResultServiceUrl(String resultServiceUrl) {
        this.resultServiceUrl = resultServiceUrl;
    }
}
