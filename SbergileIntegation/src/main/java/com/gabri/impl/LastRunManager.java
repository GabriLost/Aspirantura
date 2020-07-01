package com.gabri.impl;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class LastRunManager{

    private final static String PLUGIN_KEY = "com.gabri.";
    private PluginSettingsFactory pluginSettingsFactory;

    @Autowired
    public LastRunManager(@ComponentImport final PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    private PluginSettings getPluginsSettings() {
        return pluginSettingsFactory.createGlobalSettings();
    }

    public String getValue(String key) {
        PluginSettings pluginSettings = getPluginsSettings();
        Object obj = pluginSettings.get(PLUGIN_KEY+key);
        if (obj != null) {
            return obj.toString();
        }
        return "";
    }

    public void setValue(String key, String value) {
        getPluginsSettings().put(PLUGIN_KEY+key, value);
    }
}
