package ru.sbertech.atlas.jira.cupintegration.in.model;

import ru.sbertech.atlas.jira.cupintegration.annotation.Validator;
import ru.sbertech.atlas.jira.cupintegration.validator.QuartzFieldValidator;

public class ImportSettings {
    /**
     * Folder where plugin gets data for import issues
     */
    public String importFolder;
    /**
     * Quartz expression for auto-import issues from Cup to Jira
     */
    @Validator(clazz = QuartzFieldValidator.class)
    public String quartzExpression;
    /**
     * If <tt>true</tt> plugin will import issues automatically
     */
    public Boolean autoImportEnabled = false;
    /**
     * ID of field, which contains ZNI id from CUP
     */
    public String cupZniIdField;

    /**
     * ID of field, which contains KRP id from CUP
     */
    public String cupKrpIdField;

    /**
     * User who сreate/update issue
     */
    public String userName;

    public ImportSettings(String importFolder, String quartzExpression, boolean autoImportEnabled, String cupZniIdField, String userName, String cupKrpIdField) {
        this.importFolder = importFolder;
        this.quartzExpression = quartzExpression;
        this.autoImportEnabled = autoImportEnabled;
        this.cupZniIdField = cupZniIdField;
        this.userName = userName;
        this.cupKrpIdField = cupKrpIdField;
    }

    public ImportSettings() {
    }

    public String getImportFolder() {
        return importFolder;
    }

    public String getQuartzExpression() {
        return quartzExpression;
    }

    public Boolean getAutoImportEnabled() {
        return autoImportEnabled;
    }

    public String getCupZniIdField() {
        return cupZniIdField;
    }

    public String getCupKrpIdField() {
        return cupKrpIdField;
    }

    public String getUserName() {
        return userName;
    }
}
