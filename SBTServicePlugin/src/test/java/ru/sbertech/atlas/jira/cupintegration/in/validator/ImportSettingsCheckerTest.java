package ru.sbertech.atlas.jira.cupintegration.in.validator;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
/**
 * Created by Yaroslav Astafiev on 18/03/2016.
 * Department of analytical solutions and system services improvement.
 */
@RunWith(PowerMockRunner.class)
public class ImportSettingsCheckerTest {

    //TODO: rewrite EXPECTED EXCEPTION on THROWN
    @Test(expected = Exception.class)
    @PrepareForTest({ComponentAccessor.class})
    public void testCheckCustomField() throws Exception {
        CustomFieldManager customFieldManager = mock(CustomFieldManager.class);
        when(customFieldManager.getCustomFieldObject(1L)).thenReturn(mock(CustomField.class));
        when(customFieldManager.getCustomFieldObject(0L)).thenReturn(null);
        PowerMockito.mockStatic(ComponentAccessor.class);
        when(ComponentAccessor.getCustomFieldManager()).thenReturn(customFieldManager);

        ImportSettingsChecker importSettingsChecker = new ImportSettingsChecker();

        //verify normal field
        assertEquals(importSettingsChecker, importSettingsChecker.checkCustomField("1"));
        //verify throws
        importSettingsChecker.checkCustomField("0");
    }

    //TODO: rewrite EXPECTED EXCEPTION on THROWN
    @Test(expected = Exception.class)
    @PrepareForTest({ComponentAccessor.class})
    public void testCheckUserName() throws Exception {
        UserManager userManager = mock(UserManager.class);
        when(userManager.getUserByName("admin")).thenReturn(mock(ApplicationUser.class));
        when(userManager.getUserByName("non-exist")).thenReturn(null);
        PowerMockito.mockStatic(ComponentAccessor.class);
        when(ComponentAccessor.getUserManager()).thenReturn(userManager);

        ImportSettingsChecker importSettingsChecker = new ImportSettingsChecker();

        //verify normal field
        assertEquals(importSettingsChecker, importSettingsChecker.checkUserName("admin"));
        //verify throws
        importSettingsChecker.checkUserName("non-exist");
    }

    //TODO: rewrite EXPECTED EXCEPTION on THROWN
    @Test(expected = Exception.class)
    public void testCheckReleaseMapping_Check_Null() throws Exception {
        new ImportSettingsChecker().checkReleaseMapping(null);
    }

    //TODO: rewrite EXPECTED EXCEPTION on THROWN
    @Test(expected = Exception.class)
    public void testCheckReleaseMapping_Check_EmptyString_Param() throws Exception {
        ReleaseMapping releaseMapping = new ReleaseMapping("someData", "someData", "someData", "someData", "someData", "");
        new ImportSettingsChecker().checkReleaseMapping(releaseMapping);
    }

    //TODO: rewrite EXPECTED EXCEPTION on THROWN
    @Test(expected = Exception.class)
    public void checkReleaseMapping_Check_Null_Param() throws Exception {
        ReleaseMapping releaseMapping = new ReleaseMapping("someData", "someData", "someData", "someData", "someData", null);
        new ImportSettingsChecker().checkReleaseMapping(releaseMapping);
    }

    @Test
    public void testCheckReleaseMapping_Check_Ok() throws Exception {
        ReleaseMapping releaseMapping = new ReleaseMapping("someData", "someData", "someData", "someData", "someData", "someData");

        ImportSettingsChecker importSettingsChecker = new ImportSettingsChecker();

        assertEquals(importSettingsChecker, importSettingsChecker.checkReleaseMapping(releaseMapping));
    }
}
