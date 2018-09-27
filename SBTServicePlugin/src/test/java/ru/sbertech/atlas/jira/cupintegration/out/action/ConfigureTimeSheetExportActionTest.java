package ru.sbertech.atlas.jira.cupintegration.out.action;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import org.junit.Before;
import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.out.TimesheetGenerator;
import ru.sbertech.atlas.jira.cupintegration.out.model.PluginSettingsHolder;
import ru.sbertech.atlas.jira.cupintegration.out.service.PluginSettingsService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dmitriev Vladimir
 */
public class ConfigureTimeSheetExportActionTest {
    private PluginSettingsService mockPluginSettingsService;
    private TimesheetGenerator mockTimesheetGenerator;

    @Before
    public void setup() {
        mockPluginSettingsService = mock(PluginSettingsService.class);
        mockTimesheetGenerator = mock(TimesheetGenerator.class);
    }

    @Test(expected = NullPointerException.class)
    public void testConfigureTimeSheetExportAction_NullArguments_NullPointerException() {
        new ConfigureTimeSheetExportAction(null, null);
    }

    @Test
    public void testConfigureTimeSheetExportAction_NullPluginSettingsFields() {
        PluginSettingsHolder pluginSettingsHolder = new PluginSettingsHolder(null, null, null, null, null, null, null, null, null, null);

        when(mockPluginSettingsService.getPluginSettings()).thenReturn(pluginSettingsHolder);

        ConfigureTimeSheetExportAction configureTimeSheetExportAction = new ConfigureTimeSheetExportAction(mockPluginSettingsService, mockTimesheetGenerator);

        verify(mockPluginSettingsService, times(1)).getPluginSettings();

        assertNull(configureTimeSheetExportAction.getSharedFolder());
        assertNull(configureTimeSheetExportAction.getAutoExport());
        assertNull(configureTimeSheetExportAction.getSchedule());
        assertNull(configureTimeSheetExportAction.getFilter());
        assertNull(configureTimeSheetExportAction.getFilePrefix());
        assertNull(configureTimeSheetExportAction.getExportSmbWebService());
        assertNull(configureTimeSheetExportAction.getExportSmbLogin());
        assertNull(configureTimeSheetExportAction.getExportSmbPassword());
        assertNull(configureTimeSheetExportAction.getExportPathSmb());
        assertNull(configureTimeSheetExportAction.getResultServiceUrl());
    }

    @Test
    public void testConfigureTimeSheetExportAction_NotNullPluginSettingsFields() {
        String sharedFolder = "sharedFolder";
        String autoExport = "autoExport";
        String schedule = "schedule";
        String filter = "filter";
        String filePrefix = "filePrefix";
        String exportSmbWebService = "exportSmbWebService";
        String exportSmbLogin = "exportSmbLogin";
        String exportSmbPassword = "exportSmbPassword";
        String exportPathSmb = "exportPathSmb";
        String resultServiceUrl = "resultServiceUrl";

        PluginSettingsHolder pluginSettingsHolder = new PluginSettingsHolder(sharedFolder, autoExport, schedule, filter, filePrefix, exportSmbWebService, exportSmbLogin,
            exportSmbPassword, exportPathSmb, resultServiceUrl);

        when(mockPluginSettingsService.getPluginSettings()).thenReturn(pluginSettingsHolder);

        ConfigureTimeSheetExportAction configureTimeSheetExportAction = new ConfigureTimeSheetExportAction(mockPluginSettingsService, mockTimesheetGenerator);

        verify(mockPluginSettingsService, times(1)).getPluginSettings();

        assertEquals(sharedFolder, configureTimeSheetExportAction.getSharedFolder());
        assertEquals(autoExport, configureTimeSheetExportAction.getAutoExport());
        assertEquals(schedule, configureTimeSheetExportAction.getSchedule());
        assertEquals(filter, configureTimeSheetExportAction.getFilter());
        assertEquals(filePrefix, configureTimeSheetExportAction.getFilePrefix());
        assertEquals(exportSmbWebService, configureTimeSheetExportAction.getExportSmbWebService());
        assertEquals(exportSmbLogin, configureTimeSheetExportAction.getExportSmbLogin());
        assertEquals(exportSmbPassword, configureTimeSheetExportAction.getExportSmbPassword());
        assertEquals(exportPathSmb, configureTimeSheetExportAction.getExportPathSmb());
        assertEquals(resultServiceUrl, configureTimeSheetExportAction.getResultServiceUrl());
    }

    @Test
    public void testDoDefault() throws Exception {
        String sharedFolder = "sharedFolder";
        String autoExport = "autoExport";
        String schedule = "schedule";
        String filter = "filter";
        String filePrefix = "filePrefix";
        String exportSmbWebService = "exportSmbWebService";
        String exportSmbLogin = "exportSmbLogin";
        String exportSmbPassword = "exportSmbPassword";
        String exportPathSmb = "exportPathSmb";
        String resultServiceUrl = "resultServiceUrl";

        PluginSettingsHolder pluginSettingsHolder = new PluginSettingsHolder(sharedFolder, autoExport, schedule, filter, filePrefix, exportSmbWebService, exportSmbLogin,
            exportSmbPassword, exportPathSmb, resultServiceUrl);

        when(mockPluginSettingsService.getPluginSettings()).thenReturn(pluginSettingsHolder);

        doNothing().when(mockPluginSettingsService).setSettings(any(PluginSettingsHolder.class));

        doNothing().when(mockTimesheetGenerator).reschedulePeriodicExport(any(PluginSettingsHolder.class));

        ConfigureTimeSheetExportAction configureTimeSheetExportAction = new ConfigureTimeSheetExportAction(mockPluginSettingsService, mockTimesheetGenerator);
        String result = configureTimeSheetExportAction.doDefault();

        verify(mockPluginSettingsService, times(1)).getPluginSettings();
        verify(mockPluginSettingsService, times(1)).setSettings(any(PluginSettingsHolder.class));
        verify(mockTimesheetGenerator, times(1)).reschedulePeriodicExport(any(PluginSettingsHolder.class));
        assertEquals(JiraWebActionSupport.INPUT, result);
        assertFalse(configureTimeSheetExportAction.hasAnyErrors());
    }

    @Test
    public void testDoDefault_Exception() throws Exception {
        String sharedFolder = "sharedFolder";
        String autoExport = "autoExport";
        String schedule = "schedule";
        String filter = "filter";
        String filePrefix = "filePrefix";
        String exportSmbWebService = "exportSmbWebService";
        String exportSmbLogin = "exportSmbLogin";
        String exportSmbPassword = "exportSmbPassword";
        String exportPathSmb = "exportPathSmb";
        String resultServiceUrl = "resultServiceUrl";

        PluginSettingsHolder pluginSettingsHolder = new PluginSettingsHolder(sharedFolder, autoExport, schedule, filter, filePrefix, exportSmbWebService, exportSmbLogin,
            exportSmbPassword, exportPathSmb, resultServiceUrl);

        when(mockPluginSettingsService.getPluginSettings()).thenReturn(pluginSettingsHolder);

        doNothing().when(mockPluginSettingsService).setSettings(any(PluginSettingsHolder.class));

        doThrow(new RuntimeException("ReschedulePeriodicExport: SomeException")).when(mockTimesheetGenerator).reschedulePeriodicExport(any(PluginSettingsHolder.class));

        ConfigureTimeSheetExportAction configureTimeSheetExportAction = new ConfigureTimeSheetExportAction(mockPluginSettingsService, mockTimesheetGenerator);
        String result = configureTimeSheetExportAction.doDefault();

        verify(mockPluginSettingsService, times(1)).getPluginSettings();
        verify(mockPluginSettingsService, times(1)).setSettings(any(PluginSettingsHolder.class));
        verify(mockTimesheetGenerator, times(1)).reschedulePeriodicExport(any(PluginSettingsHolder.class));
        assertEquals(JiraWebActionSupport.INPUT, result);
        assertTrue(configureTimeSheetExportAction.hasAnyErrors());
    }

    @Test
    public void testSettersAndGetters() {
        String sharedFolder = "sharedFolder";
        String autoExport = "autoExport";
        String schedule = "schedule";
        String filter = "filter";
        String filePrefix = "filePrefix";
        String exportSmbWebService = "exportSmbWebService";
        String exportSmbLogin = "exportSmbLogin";
        String exportSmbPassword = "exportSmbPassword";
        String exportPathSmb = "exportPathSmb";
        String resultServiceUrl = "resultServiceUrl";

        PluginSettingsHolder pluginSettingsHolder = new PluginSettingsHolder(null, null, null, null, null, null, null, null, null, null);

        when(mockPluginSettingsService.getPluginSettings()).thenReturn(pluginSettingsHolder);

        ConfigureTimeSheetExportAction configureTimeSheetExportAction = new ConfigureTimeSheetExportAction(mockPluginSettingsService, mockTimesheetGenerator);

        verify(mockPluginSettingsService, times(1)).getPluginSettings();

        configureTimeSheetExportAction.setSharedFolder(sharedFolder);
        configureTimeSheetExportAction.setAutoExport(autoExport);
        configureTimeSheetExportAction.setSchedule(schedule);
        configureTimeSheetExportAction.setFilter(filter);
        configureTimeSheetExportAction.setFilePrefix(filePrefix);
        configureTimeSheetExportAction.setExportSmbWebService(exportSmbWebService);
        configureTimeSheetExportAction.setExportSmbLogin(exportSmbLogin);
        configureTimeSheetExportAction.setExportSmbPassword(exportSmbPassword);
        configureTimeSheetExportAction.setExportPathSmb(exportPathSmb);
        configureTimeSheetExportAction.setResultServiceUrl(resultServiceUrl);

        assertEquals(sharedFolder, configureTimeSheetExportAction.getSharedFolder());
        assertEquals(autoExport, configureTimeSheetExportAction.getAutoExport());
        assertEquals(schedule, configureTimeSheetExportAction.getSchedule());
        assertEquals(filter, configureTimeSheetExportAction.getFilter());
        assertEquals(filePrefix, configureTimeSheetExportAction.getFilePrefix());
        assertEquals(exportSmbWebService, configureTimeSheetExportAction.getExportSmbWebService());
        assertEquals(exportSmbLogin, configureTimeSheetExportAction.getExportSmbLogin());
        assertEquals(exportSmbPassword, configureTimeSheetExportAction.getExportSmbPassword());
        assertEquals(exportPathSmb, configureTimeSheetExportAction.getExportPathSmb());
        assertEquals(resultServiceUrl, configureTimeSheetExportAction.getResultServiceUrl());
    }
}
