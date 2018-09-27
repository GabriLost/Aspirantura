//package ru.gabri.dao;
//
//import com.atlassian.sal.api.pluginsettings.PluginSettings;
//import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
//import ru.gabri.exception.ImportSettingsUpdateException;
//
//import java.util.concurrent.locks.ReentrantLock;
//
//public class ReleaseMappingServiceJiraStorage implements ReleaseMappingService {
//
//    private final PluginSettings pluginSettings;
//    private final ReentrantLock lock = new ReentrantLock();
//    private static String PLUGIN_KEY = "GABRI_PLUGIN";
//    public ReleaseMappingServiceJiraStorage(PluginSettingsFactory pluginSettingsFactory) {
//        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
//    }
//
//    @Override
//    public String getValue() {
//        return (String) pluginSettings.get(PLUGIN_KEY);
//    }
//
//    @Override
//    public void createOrUpdateValue(String value) throws ImportSettingsUpdateException {
//        lock.lock();
//        try {
//            pluginSettings.put(PLUGIN_KEY , value);
//        } finally {
//            lock.unlock();
//        }
//    }
//}
