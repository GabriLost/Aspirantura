package ru.sbertech.atlas.jira.cupintegration.out.action;

import org.junit.Before;
import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.out.model.PluginSettingsHolder;
import ru.sbertech.atlas.jira.cupintegration.out.service.PluginSettingsService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dmitriev Vladimir
 */
public class ManualTimeSheetExportActionTest {
    private PluginSettingsService mockPluginSettingsService;

    @Before
    public void setup() {
        mockPluginSettingsService = mock(PluginSettingsService.class);
    }

    @Test
    public void testManualTimeSheetExport_NullFilter() {
        PluginSettingsHolder mockPluginSettingsHolder = new PluginSettingsHolder(null, null, null, null, null, null, null, null, null, null);
        when(mockPluginSettingsService.getPluginSettings()).thenReturn(mockPluginSettingsHolder);

        ManualTimeSheetExportAction manualTimeSheetExportAction = new ManualTimeSheetExportAction(mockPluginSettingsService);

        verify(mockPluginSettingsService, times(1)).getPluginSettings();

        assertNull(manualTimeSheetExportAction.getFilter());
        assertNull(manualTimeSheetExportAction.getFromDate());
        assertNull(manualTimeSheetExportAction.getToDate());
        assertNull(manualTimeSheetExportAction.getXml());
    }

    @Test
    public void testManualTimeSheetExport_NotNullFilter() {
        String filter = "filter";
        PluginSettingsHolder mockPluginSettingsHolder = new PluginSettingsHolder(null, null, null, filter, null, null, null, null, null, null);
        when(mockPluginSettingsService.getPluginSettings()).thenReturn(mockPluginSettingsHolder);

        ManualTimeSheetExportAction manualTimeSheetExportAction = new ManualTimeSheetExportAction(mockPluginSettingsService);

        verify(mockPluginSettingsService, times(1)).getPluginSettings();

        assertEquals(filter, manualTimeSheetExportAction.getFilter());
        assertNull(manualTimeSheetExportAction.getFromDate());
        assertNull(manualTimeSheetExportAction.getToDate());
        assertNull(manualTimeSheetExportAction.getXml());
    }

    @Test(expected = NullPointerException.class)
    public void testManualTimeSheetExport_Null_NullPointerException() {
        new ManualTimeSheetExportAction(null);
    }

    @Test
    public void testSettersAndGetters() {
        String xml = "testXml";
        String filter = "testFilter";
        String fromDate = "testFromDate";
        String toDate = "testToDate";

        PluginSettingsHolder mockPluginSettingsHolder = new PluginSettingsHolder(null, null, null, null, null, null, null, null, null, null);
        when(mockPluginSettingsService.getPluginSettings()).thenReturn(mockPluginSettingsHolder);

        ManualTimeSheetExportAction manualTimeSheetExportAction = new ManualTimeSheetExportAction(mockPluginSettingsService);

        verify(mockPluginSettingsService, times(1)).getPluginSettings();

        manualTimeSheetExportAction.setXml(xml);
        manualTimeSheetExportAction.setFilter(filter);
        manualTimeSheetExportAction.setFromDate(fromDate);
        manualTimeSheetExportAction.setToDate(toDate);

        assertEquals(xml, manualTimeSheetExportAction.getXml());
        assertEquals(filter, manualTimeSheetExportAction.getFilter());
        assertEquals(fromDate, manualTimeSheetExportAction.getFromDate());
        assertEquals(toDate, manualTimeSheetExportAction.getToDate());
    }
}
