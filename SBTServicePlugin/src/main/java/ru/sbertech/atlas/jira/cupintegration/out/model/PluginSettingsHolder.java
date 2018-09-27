package ru.sbertech.atlas.jira.cupintegration.out.model;

import java.io.Serializable;

public class PluginSettingsHolder implements Serializable {
    public static final String PLUGIN_PREFIX = "CupIntegrationPlugin";
    public static final String TIMESHEET_EXPORT_SETTINGS_AUTO_EXPORT = PLUGIN_PREFIX + "autoExport";
    public static final String TIMESHEET_EXPORT_SETTINGS_FILTER = PLUGIN_PREFIX + "filter";
    public static final String TIMESHEET_EXPORT_SETTINGS_SCHEDULE = PLUGIN_PREFIX + "schedule";
    public static final String TIMESHEET_EXPORT_SETTINGS_SHARED_FOLDER = PLUGIN_PREFIX + "sharedFolder";
    public static final String TIMESHEET_EXPORT_SETTINGS_FILE_PREFIX = PLUGIN_PREFIX + "filePrefix";
    public static final String TIMESHEET_EXPORT_SETTINGS_SMB_WEB_SERVICE = PLUGIN_PREFIX + "exportSmbWebService";
    public static final String TIMESHEET_EXPORT_SETTINGS_SMB_LOGIN = PLUGIN_PREFIX + "exportSmbLogin";
    public static final String TIMESHEET_EXPORT_SETTINGS_SMB_PASSWORD = PLUGIN_PREFIX + "exportSmbPassword";
    public static final String TIMESHEET_EXPORT_SETTINGS_SMB_PATH = PLUGIN_PREFIX + "exportPathSmb";
    public static final String TIMESHEET_EXPORT_SETTINGS_RESULT_SERVICE_URL = PLUGIN_PREFIX + "resultServiceUrl";
    private static final long serialVersionUID = 7818350185548434642L;
    /**
     * String representation of shared folder location for CUP integration
     */
    public final String sharedFolder;
    /**
     * String representation of boolean value that defines is auto export enabled
     */
    public final String autoExport;
    /**
     * String representation of quartz auto export schedule
     */
    public final String schedule;
    /**
     *
     * @return String representation of jql issues filter
     */
    public final String filter;
    /**
     *
     * @return String representation of output time sheets file name prefix
     */
    public final String filePrefix;
    /**
     * @return String representation of url smb web-service
     */
    public final String exportSmbWebService;
    /**
     * @return String representation of smb login
     */
    public final String exportSmbLogin;
    /**
     * @return String representation of smb password
     */
    public final String exportSmbPassword;
    /**
     * @return String representation of path to smb folder
     */
    public final String exportPathSmb;
    /**
     * @return String representation of result service url for get answer from rest transfer service
     */
    public final String resultServiceUrl;
    /**
     * Constructor for Settings pojo class.
     * @param sharedFolder String representation of shared folder location for CUP integration
     * @param autoExport String representation of boolean value. Defines is auto export enabled
     * @param schedule String representation of quartz schedule
     * @param filter String representation of jql issues filter
     * @param filePrefix String representation of output time sheets file name prefix
     * @param exportSmbWebService
     * @param exportSmbLogin
     * @param exportSmbPassword
     * @param exportPathSmb
     * @param resultServiceUrl
     */
    public PluginSettingsHolder(String sharedFolder, String autoExport, String schedule, String filter, String filePrefix, String exportSmbWebService, String exportSmbLogin,
        String exportSmbPassword, String exportPathSmb, String resultServiceUrl) {
        this.sharedFolder = sharedFolder;
        this.autoExport = autoExport;
        this.schedule = schedule;
        this.filter = filter;
        this.filePrefix = filePrefix;
        this.exportSmbWebService = exportSmbWebService;
        this.exportSmbLogin = exportSmbLogin;
        this.exportSmbPassword = exportSmbPassword;
        this.exportPathSmb = exportPathSmb;
        this.resultServiceUrl = resultServiceUrl;
    }
}
