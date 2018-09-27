package ru.sbertech.atlas.jira.cupintegration.out.action;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.sbertech.atlas.jira.cupintegration.out.service.PluginSettingsService;

public class ManualTimeSheetExportAction extends JiraWebActionSupport {

    private String xml;
    private String filter;
    private String fromDate;
    private String toDate;

    public ManualTimeSheetExportAction(PluginSettingsService pluginSettingsService) {

        filter = pluginSettingsService.getPluginSettings().filter;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
