package ru.sbertech.atlas.jira.cupintegration.out.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PluginSettingsHolderTest {

    @Test
    public void testGetSharedFolder() {
        String sharedFolder = "some/shared/folder";
        String autoExport = "true";
        String schedule = "* * * * ?";
        String filter = "project = TEST";
        String filePrefix = "someFileName";
        String exportSmbWebService = "exportSmbWebService";
        String exportSmbLogin = "exportSmbLogin";
        String exportSmbPassword = "exportSmbPassword";
        String exportPathSmb = "exportPathSmb";
        String resultServiceUrl = "http://null";

        PluginSettingsHolder psh =
            new PluginSettingsHolder(sharedFolder, autoExport, schedule, filter, filePrefix, exportSmbWebService, exportSmbLogin, exportSmbPassword, exportPathSmb,
                resultServiceUrl);

        assertEquals(sharedFolder, psh.sharedFolder);
        assertEquals(autoExport, psh.autoExport);
        assertEquals(schedule, psh.schedule);
        assertEquals(filter, psh.filter);
        assertEquals(filePrefix, psh.filePrefix);
        assertEquals(exportSmbWebService, psh.exportSmbWebService);
        assertEquals(exportSmbLogin, psh.exportSmbLogin);
        assertEquals(exportSmbPassword, psh.exportSmbPassword);
        assertEquals(exportPathSmb, psh.exportPathSmb);
        assertEquals(resultServiceUrl, psh.resultServiceUrl);
    }
}
