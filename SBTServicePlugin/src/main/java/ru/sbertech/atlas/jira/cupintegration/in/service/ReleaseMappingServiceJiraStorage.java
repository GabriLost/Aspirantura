package ru.sbertech.atlas.jira.cupintegration.in.service;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportSettingsUpdateException;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.out.model.PluginSettingsHolder;
import ru.sbertech.atlas.jira.cupintegration.validator.ValidationProcessor;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Dmitriev Vladimir
 */
public class ReleaseMappingServiceJiraStorage implements ReleaseMappingService {
    public static final String PPM_RELEASE_ID = PluginSettingsHolder.PLUGIN_PREFIX + "ppmReleaseId";
    public static final String PPM_RELEASE_NAME = PluginSettingsHolder.PLUGIN_PREFIX + "ppmReleaseName";
    public static final String PPM_RELEASE_AREA_PS = PluginSettingsHolder.PLUGIN_PREFIX + "ppmReleaseAreaPs";
    public static final String PPM_RELEASE_START_DATE = PluginSettingsHolder.PLUGIN_PREFIX + "ppmReleaseStartDate";
    public static final String PPM_RELEASE_FINISH_DATE = PluginSettingsHolder.PLUGIN_PREFIX + "ppmReleaseFinishDate";
    public static final String PPM_RELEASE_STATUS = PluginSettingsHolder.PLUGIN_PREFIX + "ppmReleaseStatus";

    private final PluginSettings pluginSettings;
    private final ReentrantLock lock = new ReentrantLock();

    public ReleaseMappingServiceJiraStorage(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    @Override
    public ReleaseMapping getReleaseMapping() {
        return new ReleaseMapping((String) pluginSettings.get(PPM_RELEASE_ID), (String) pluginSettings.get(PPM_RELEASE_NAME), (String) pluginSettings.get(PPM_RELEASE_AREA_PS),
            (String) pluginSettings.get(PPM_RELEASE_START_DATE), (String) pluginSettings.get(PPM_RELEASE_FINISH_DATE), (String) pluginSettings.get(PPM_RELEASE_STATUS));
    }

    @Override
    public void createOrUpdateReleaseMapping(ReleaseMapping releaseMapping) throws ImportSettingsUpdateException {
        lock.lock();
        try {
            new ValidationProcessor<ReleaseMapping>().validateSetting(releaseMapping);
            pluginSettings.put(PPM_RELEASE_ID, releaseMapping.ppmReleaseId);
            pluginSettings.put(PPM_RELEASE_NAME, releaseMapping.ppmReleaseName);
            pluginSettings.put(PPM_RELEASE_AREA_PS, releaseMapping.ppmReleaseAreaPs);
            pluginSettings.put(PPM_RELEASE_START_DATE, releaseMapping.ppmReleaseStartDate);
            pluginSettings.put(PPM_RELEASE_FINISH_DATE, releaseMapping.ppmReleaseFinishDate);
            pluginSettings.put(PPM_RELEASE_STATUS, releaseMapping.ppmReleaseStatus);
        } finally {
            lock.unlock();
        }
    }
}
