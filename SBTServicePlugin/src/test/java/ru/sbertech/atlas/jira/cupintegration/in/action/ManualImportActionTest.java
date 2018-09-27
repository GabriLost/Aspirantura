package ru.sbertech.atlas.jira.cupintegration.in.action;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Dmitriev Vladimir
 */
public class ManualImportActionTest {
    @Test
    public void testManualImportAction() {
        ManualImportAction manualImportAction = new ManualImportAction();

        assertNotNull(manualImportAction);
    }
}
