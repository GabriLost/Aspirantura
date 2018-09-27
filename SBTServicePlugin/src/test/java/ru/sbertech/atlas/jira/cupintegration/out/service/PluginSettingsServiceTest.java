package ru.sbertech.atlas.jira.cupintegration.out.service;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.testresources.pluginsettings.MockPluginSettingsFactory;
import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.out.model.PluginSettingsHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PluginSettingsServiceTest {

    @Test
    public void testGetPluginSettings_Null() throws Exception {
        PluginSettingsFactory pluginSettingsFactory = new MockPluginSettingsFactory();

        PluginSettingsService pluginSettingsService = new PluginSettingsService(pluginSettingsFactory);

        assertNull(pluginSettingsService.getPluginSettings().sharedFolder);
        assertNull(pluginSettingsService.getPluginSettings().autoExport);
        assertNull(pluginSettingsService.getPluginSettings().schedule);
        assertNull(pluginSettingsService.getPluginSettings().filter);
        assertNull(pluginSettingsService.getPluginSettings().filePrefix);
    }

    @Test
    public void testGetPluginSettings_NotNull() throws Exception {
        PluginSettingsFactory pluginSettingsFactory = new MockPluginSettingsFactory();
        PluginSettingsHolder pluginSettingsHolder =
            new PluginSettingsHolder("String", "true", "* * * * * ?", "project = TEST", "prefix", "service", "admin", "12345", "tmp", "http://null");

        PluginSettingsService pluginSettingsService= new PluginSettingsService(pluginSettingsFactory);
        pluginSettingsService.setSettings(pluginSettingsHolder);

        assertEquals("String", pluginSettingsService.getPluginSettings().sharedFolder);
        assertEquals("true", pluginSettingsService.getPluginSettings().autoExport);
        assertEquals("* * * * * ?", pluginSettingsService.getPluginSettings().schedule);
        assertEquals("project = TEST", pluginSettingsService.getPluginSettings().filter);
        assertEquals("prefix", pluginSettingsService.getPluginSettings().filePrefix);
        assertEquals("service", pluginSettingsService.getPluginSettings().exportSmbWebService);
        assertEquals("admin", pluginSettingsService.getPluginSettings().exportSmbLogin);
        assertEquals("12345", pluginSettingsService.getPluginSettings().exportSmbPassword);
        assertEquals("tmp", pluginSettingsService.getPluginSettings().exportPathSmb);
        assertEquals("http://null", pluginSettingsService.getPluginSettings().resultServiceUrl);
    }

    @Test
    public void testSetSettings_Override() throws Exception {
        PluginSettingsFactory pluginSettingsFactory = new MockPluginSettingsFactory();
        PluginSettingsHolder pluginSettingsHolder = new PluginSettingsHolder(null, null, null, null, null, null, null, null, null, null);
        PluginSettingsHolder pluginSettingsHolder2 =
            new PluginSettingsHolder("String", "true", "* * * * * ?", "project = TEST", "prefix", "service", "admin", "12345", "tmp", "http://null");

        PluginSettingsService pluginSettingsService= new PluginSettingsService(pluginSettingsFactory);
        pluginSettingsService.setSettings(pluginSettingsHolder);
        pluginSettingsService.setSettings(pluginSettingsHolder2);

        assertEquals("String", pluginSettingsService.getPluginSettings().sharedFolder);
        assertEquals("true", pluginSettingsService.getPluginSettings().autoExport);
        assertEquals("* * * * * ?", pluginSettingsService.getPluginSettings().schedule);
        assertEquals("project = TEST", pluginSettingsService.getPluginSettings().filter);
        assertEquals("prefix", pluginSettingsService.getPluginSettings().filePrefix);
        assertEquals("service", pluginSettingsService.getPluginSettings().exportSmbWebService);
        assertEquals("admin", pluginSettingsService.getPluginSettings().exportSmbLogin);
        assertEquals("12345", pluginSettingsService.getPluginSettings().exportSmbPassword);
        assertEquals("tmp", pluginSettingsService.getPluginSettings().exportPathSmb);
        assertEquals("http://null", pluginSettingsService.getPluginSettings().resultServiceUrl);
    }

}
