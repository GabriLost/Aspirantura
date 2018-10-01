package ru.gabri.dao;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LastRunStorage implements LastRun{

    private final PluginSettingsFactory pluginSettingsFactory;
    private static String PLUGIN_KEY = "GABRI_PLUGIN";
    @Autowired
    public LastRunStorage(PluginSettingsFactory pluginSettingsFactory){
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public void setValue(String key, String value){
           pluginSettingsFactory.createGlobalSettings().put(PLUGIN_KEY+key, value);
    }

    public String getValue(String key) {
        return String.valueOf(pluginSettingsFactory.createGlobalSettings().get(PLUGIN_KEY+key));
    }
}
