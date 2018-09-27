package ru.sbertech.atlas.jira.cupintegration.out;

import com.atlassian.jira.component.ComponentAccessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.exception.RequiredComponentNotFoundException;
import ru.sbertech.atlas.jira.userenrich.manager.UserInfoManager;


import java.io.File;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
/**
 * Created by Yaroslav Astafiev on 18/03/2016.
 * Department of analytical solutions and system services improvement.
 */
@RunWith(PowerMockRunner.class)
public class TimesheetGeneratorCheckerTest {

    @Test(expected = RequiredComponentNotFoundException.class)
    @PrepareForTest({ComponentAccessor.class})
    public void checkUserInfoManager_MissedComponent() throws Exception {
        PowerMockito.mockStatic(ComponentAccessor.class);
        when(ComponentAccessor.getOSGiComponentInstanceOfType(UserInfoManager.class)).thenReturn(null);

        new TimesheetGeneratorChecker().checkUserInfoManager();
    }

    @Test
    @PrepareForTest({ComponentAccessor.class})
    public void testCheckUserInfoManager_ComponentExists() throws Exception {
        PowerMockito.mockStatic(ComponentAccessor.class);
        when(ComponentAccessor.getOSGiComponentInstanceOfType(UserInfoManager.class)).thenReturn(mock(UserInfoManager.class));

        TimesheetGeneratorChecker timesheetGeneratorChecker = new TimesheetGeneratorChecker();

        assertEquals(timesheetGeneratorChecker, timesheetGeneratorChecker.checkUserInfoManager());
    }

    @Test(expected = RequiredComponentNotFoundException.class)
    public void testCheckLocalDirectory_CheckDirectory_CheckFile() throws Exception {
        String filePath = getClass().getClassLoader().getResource("timesheetGeneratorCheckerFile.txt").getFile();
        File testFile = new File(filePath);
        String fileDirectory = testFile.getParentFile().getAbsolutePath();

        TimesheetGeneratorChecker timesheetGeneratorChecker = new TimesheetGeneratorChecker();

        assertEquals(timesheetGeneratorChecker, timesheetGeneratorChecker.checkLocalDirectory(fileDirectory));
        timesheetGeneratorChecker.checkLocalDirectory(filePath);
    }
}
