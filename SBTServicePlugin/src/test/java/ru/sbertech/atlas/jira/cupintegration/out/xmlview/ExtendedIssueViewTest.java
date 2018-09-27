package ru.sbertech.atlas.jira.cupintegration.out.xmlview;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.issue.views.util.IssueViewUtil;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogManager;
import com.atlassian.jira.mock.MockApplicationProperties;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.plugin.customfield.CustomFieldTypeModuleDescriptor;
import com.atlassian.jira.plugin.issueview.IssueViewFieldParams;
import com.atlassian.jira.plugin.issueview.IssueViewModuleDescriptor;
import com.atlassian.jira.plugin.issueview.IssueViewRequestParams;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.MockUser;
import com.atlassian.jira.usercompatibility.UserCompatibilityHelper;
import com.atlassian.jira.usercompatibility.UserWithKey;
import com.atlassian.jira.util.JiraVelocityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.out.model.UserContainer;
import ru.sbertech.atlas.jira.userenrich.dto.UserInfoDTO;
import ru.sbertech.atlas.jira.userenrich.dto.UserInfoDTOImpl;
import ru.sbertech.atlas.jira.userenrich.manager.UserInfoManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Yaroslav Astafiev on 15/03/2016.
 * Department of analytical solutions and system services improvement.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({JiraVelocityUtils.class, ComponentManager.class})
public class ExtendedIssueViewTest {

    private Issue mockedIssue;
    private IssueViewRequestParams issueViewRequestParams;

    @Before
    public void init() {
        //Mock Velocity Utils
        PowerMockito.mockStatic(JiraVelocityUtils.class);
        PowerMockito.when(JiraVelocityUtils.getDefaultVelocityParams(any(JiraAuthenticationContext.class))).thenReturn(new HashMap<String, Object>());
        //Mock IssueViewModuleDescriptor and ApplicationProperties
        IssueViewModuleDescriptor mockModuleDescriptor = mock(IssueViewModuleDescriptor.class);
        when(mockModuleDescriptor.getCompleteKey()).thenReturn("jira.issueviews:extendedIssueXMLView");
        new MockComponentWorker().addMock(ApplicationProperties.class, new MockApplicationProperties()).addMock(IssueViewModuleDescriptor.class, mockModuleDescriptor).init();
        //Mocking Component Manager
        PowerMockito.mockStatic(ComponentManager.class);
        PowerMockito.when(ComponentManager.getInstance()).thenReturn(mock(ComponentManager.class));
        //Mock IssueViewRequestParams
        IssueViewFieldParams issueViewFieldParams = mock(IssueViewFieldParams.class);
        issueViewRequestParams = mock(IssueViewRequestParams.class);
        when(issueViewRequestParams.getIssueViewFieldParams()).thenReturn(issueViewFieldParams);
        //Mock issue: issueType and ProjectId
        Project mockProject = new MockProject(1L);
        mockedIssue = mock(Issue.class);
        when(mockedIssue.getProjectObject()).thenReturn(mockProject);
        when(mockedIssue.getIssueTypeObject()).thenReturn(new MockIssueType("2", "Task"));
    }

    @Test
    public void testGetContent() throws Exception {
        IssueViewModuleDescriptor issueViewModuleDescriptor = mock(IssueViewModuleDescriptor.class);

        ExtendedIssueView extendedIssueView = spy(new ExtendedIssueView(null, null, null, null, null));
        doReturn(null).when(extendedIssueView).getBodyParameters(null, null);
        extendedIssueView.init(issueViewModuleDescriptor);

        extendedIssueView.getContent(null, null);

        verify(extendedIssueView, times(1)).getBody(null, null);
        verify(extendedIssueView, times(1)).getBodyParameters(null, null);
        verify(issueViewModuleDescriptor, times(1)).getHtml("view", null);
    }

    @Test
    @PrepareForTest({JiraVelocityUtils.class, ComponentManager.class})
    public void testGetBodyParameters_EmptyWorklog() throws Exception {
        //Prepare TEST DATA
        //Mock authentication context
        User mockUser = new MockUser("admin", "Admin Admin", "admin@admin.com");
        ApplicationUser mockApplicationUser = new MockApplicationUser("admin", "Admin Admin", "admin@admin.com");
        JiraAuthenticationContext mockJiraContext = new MockAuthenticationContext(mockApplicationUser);
        //Mock FieldLayoutManager
        FieldLayout mockFieldLayout = mock(FieldLayout.class);
        when(mockFieldLayout.getVisibleCustomFieldLayoutItems(any(Project.class), anyListOf(String.class))).thenReturn(new ArrayList<FieldLayoutItem>());
        FieldLayoutManager mockFieldLayoutManager = mock(FieldLayoutManager.class);
        when(mockFieldLayoutManager.getFieldLayout(any(Issue.class))).thenReturn(mockFieldLayout);
        //Mock IssueViewUtil (Jira Rendering Helpers)
        IssueViewUtil mockIssueViewUtil = mock(IssueViewUtil.class);
        //mock DateTimeFactory
        DateTimeFormatter dateTimeFormatter = mock(DateTimeFormatter.class);
        when(dateTimeFormatter.forLoggedInUser()).thenReturn(dateTimeFormatter);
        when(dateTimeFormatter.withSystemZone()).thenReturn(dateTimeFormatter);
        DateTimeFormatterFactory mockDateTimeFactory = mock(DateTimeFormatterFactory.class);
        when(mockDateTimeFactory.formatter()).thenReturn(dateTimeFormatter);
        //MockWorkLogManager
        WorklogManager mockWorklogManager = mock(WorklogManager.class);

        //perform testing
        ExtendedIssueView extendedIssueView = new ExtendedIssueView(mockJiraContext, mockFieldLayoutManager, mockIssueViewUtil, mockDateTimeFactory, mockWorklogManager);
        Map<String, Object> result = extendedIssueView.getBodyParameters(mockedIssue, issueViewRequestParams);

        //verify results
        assertEquals(extendedIssueView, result.get("xmlView"));
        assertTrue(result.containsKey("remoteUser"));
        assertEquals(0, ((Map) result.get("worklogs")).size());
        assertEquals(mockedIssue, result.get("issue"));
    }


    @Test
    @PrepareForTest({JiraVelocityUtils.class, ComponentManager.class, UserCompatibilityHelper.class})
    public void testGetBodyParameters_verifyWorklog() throws Exception {
        //Mock authentication context
        final ApplicationUser mockApplicationUser = new MockApplicationUser("admin", "Admin Admin", "admin@admin.com");
        final User mockUser = new MockUser("admin", "Admin Admin", "admin@admin.com");
        ApplicationUser mockAppUser = new MockApplicationUser("admin", "Admin Admin", "admin@admin.com");
        ApplicationUser mockAppUser2 = new MockApplicationUser("admin2", "Admin Admin", "admin2@admin.com");
        UserWithKey mockUserWithKey = mock(UserWithKey.class);
        when(mockUserWithKey.getUser()).thenReturn(mockUser);
        UserWithKey mockUserWithKey_Null = new UserWithKey() {
            @Override
            public User getUser() {
                return null;
            }

            @Override
            public String getKey() {
                return null;
            }
        };
        PowerMockito.mockStatic(UserCompatibilityHelper.class);
        PowerMockito.when(UserCompatibilityHelper.convertUserObject(mockAppUser)).thenReturn(mockUserWithKey);
        PowerMockito.when(UserCompatibilityHelper.convertUserObject(mockAppUser2)).thenReturn(mockUserWithKey_Null);
        //Mock UserInfoManager
        UserInfoDTO mockUserInfoDTO = new UserInfoDTOImpl(null, null, "1", null, null, null, null, null, null, new HashMap<String, Object>());
        UserInfoManager mockUserInfoManager = mock(UserInfoManager.class);
        when(mockUserInfoManager.getUserInfoByCrowdUser(mockUser)).thenReturn(mockUserInfoDTO);
        new MockComponentWorker().addMock(UserInfoManager.class, mockUserInfoManager).init();

        //Prepare TEST DATA
        JiraAuthenticationContext mockJiraContext = new MockAuthenticationContext(mockApplicationUser);
        //Mock FieldLayoutManager
        FieldLayout mockFieldLayout = mock(FieldLayout.class);
        when(mockFieldLayout.getVisibleCustomFieldLayoutItems(any(Project.class), anyListOf(String.class))).thenReturn(new ArrayList<FieldLayoutItem>());
        FieldLayoutManager mockFieldLayoutManager = mock(FieldLayoutManager.class);
        when(mockFieldLayoutManager.getFieldLayout(any(Issue.class))).thenReturn(mockFieldLayout);
        //Mock IssueViewUtil (Jira Rendering Helpers)
        IssueViewUtil mockIssueViewUtil = mock(IssueViewUtil.class);
        //mock DateTimeFactory
        DateTimeFormatter dateTimeFormatter = mock(DateTimeFormatter.class);
        when(dateTimeFormatter.forLoggedInUser()).thenReturn(dateTimeFormatter);
        when(dateTimeFormatter.withSystemZone()).thenReturn(dateTimeFormatter);
        DateTimeFormatterFactory mockDateTimeFactory = mock(DateTimeFormatterFactory.class);
        when(mockDateTimeFactory.formatter()).thenReturn(dateTimeFormatter);

        //Mock WorklogManager
        final Worklog mockWorkLog1 = mock(Worklog.class);
        when(mockWorkLog1.getAuthorObject()).thenReturn(mockAppUser);
        final Worklog mockWorkLogNullUser = mock(Worklog.class);
        when(mockWorkLogNullUser.getAuthorObject()).thenReturn(null);
        final Worklog mockWorkLogNullKeyUser = mock(Worklog.class);
        when(mockWorkLogNullKeyUser.getAuthorObject()).thenReturn(mockAppUser2);
        final Worklog mockWorklogThrowsException = mock(Worklog.class);
        when(mockWorklogThrowsException.getAuthorObject()).thenThrow(new RuntimeException("Test exception"));

        List<Worklog> mockWorklogs = new ArrayList<Worklog>() {{
            add(mockWorkLog1);
            add(mockWorkLogNullUser);
            add(mockWorkLogNullKeyUser);
            add(mockWorkLog1);
            add(null);
            add(mockWorklogThrowsException);
        }};

        WorklogManager mockWorklogManager = mock(WorklogManager.class);
        when(mockWorklogManager.getByIssue(any(Issue.class))).thenReturn(mockWorklogs);
        UserContainer expectedKey = new UserContainer(mockAppUser, "1");

        //perform testing
        ExtendedIssueView extendedIssueView = new ExtendedIssueView(mockJiraContext, mockFieldLayoutManager, mockIssueViewUtil, mockDateTimeFactory, mockWorklogManager);
        Map<String, Object> result = extendedIssueView.getBodyParameters(mockedIssue, issueViewRequestParams);

        //verify results
        assertEquals(extendedIssueView, result.get("xmlView"));
        assertTrue(result.containsKey("remoteUser"));
        List worklogList = (List) ((Map) result.get("worklogs")).get(expectedKey);
        assertEquals(2, worklogList.size());
        assertEquals(1, ((Map) result.get("worklogs")).size());
        assertEquals(mockedIssue, result.get("issue"));
    }

    //TODO: rewrite EXPECTED EXCEPTION on THROWN
    @Test(expected = RuntimeException.class)
    @PrepareForTest({JiraVelocityUtils.class, ComponentManager.class, UserCompatibilityHelper.class})
    public void testGetBodyParameters_verifyWorklog_Throws_Runtime() throws Exception {
        //Mock authentication context
        final ApplicationUser mockApplicationUser = new MockApplicationUser("admin", "Admin Admin", "admin@admin.com");
        ApplicationUser mockAppUser = new MockApplicationUser("admin", "Admin Admin", "admin@admin.com");
        UserWithKey mockUserWithKey = mock(UserWithKey.class);
        final User mockUser = new MockUser("admin", "Admin Admin", "admin@admin.com");
        when(mockUserWithKey.getUser()).thenReturn(mockUser);
        PowerMockito.mockStatic(UserCompatibilityHelper.class);
        PowerMockito.when(UserCompatibilityHelper.convertUserObject(mockAppUser).getUser()).thenReturn(mockUser);
        //Prepare TEST DATA
        JiraAuthenticationContext mockJiraContext = new MockAuthenticationContext(mockApplicationUser);
        //Mock FieldLayoutManager
        FieldLayout mockFieldLayout = mock(FieldLayout.class);
        when(mockFieldLayout.getVisibleCustomFieldLayoutItems(any(Project.class), anyListOf(String.class))).thenReturn(new ArrayList<FieldLayoutItem>());
        FieldLayoutManager mockFieldLayoutManager = mock(FieldLayoutManager.class);
        when(mockFieldLayoutManager.getFieldLayout(any(Issue.class))).thenReturn(mockFieldLayout);
        //Mock IssueViewUtil (Jira Rendering Helpers)
        IssueViewUtil mockIssueViewUtil = mock(IssueViewUtil.class);
        //mock DateTimeFactory
        DateTimeFormatter dateTimeFormatter = mock(DateTimeFormatter.class);
        when(dateTimeFormatter.forLoggedInUser()).thenReturn(dateTimeFormatter);
        when(dateTimeFormatter.withSystemZone()).thenReturn(dateTimeFormatter);
        DateTimeFormatterFactory mockDateTimeFactory = mock(DateTimeFormatterFactory.class);
        when(mockDateTimeFactory.formatter()).thenReturn(dateTimeFormatter);

        //Mock WorklogManager
        final Worklog mockWorkLog1 = mock(Worklog.class);
        when(mockWorkLog1.getAuthorObject()).thenReturn(mockAppUser);

        List<Worklog> mockWorklogs = new ArrayList<Worklog>() {{
            add(mockWorkLog1);
        }};

        WorklogManager mockWorklogManager = mock(WorklogManager.class);
        when(mockWorklogManager.getByIssue(any(Issue.class))).thenReturn(mockWorklogs);

        //perform testing
        ExtendedIssueView extendedIssueView = new ExtendedIssueView(mockJiraContext, mockFieldLayoutManager, mockIssueViewUtil, mockDateTimeFactory, mockWorklogManager);
        extendedIssueView.getBodyParameters(mockedIssue, issueViewRequestParams);

    }

    @Test
    public void testGetPrettyDurationAndRenderedContent() throws Exception {
        IssueViewUtil issueViewUtil = mock(IssueViewUtil.class);
        when(issueViewUtil.getPrettyDuration(anyLong())).thenReturn(null);
        when(issueViewUtil.getRenderedContent(anyString(), anyString(), any(Issue.class))).thenReturn(null);

        ExtendedIssueView extendedIssueView = spy(new ExtendedIssueView(null, null, issueViewUtil, null, null));
        extendedIssueView.getPrettyDuration(1L);
        extendedIssueView.getRenderedContent("Some", "String", mockedIssue);

        verify(issueViewUtil, times(1)).getRenderedContent(anyString(), anyString(), any(Issue.class));
        verify(issueViewUtil, times(1)).getPrettyDuration(anyLong());
    }

    @Test
    public void testGetCustomFieldXML() throws Exception {
        //prepare test data
        //Mock FieldLayoutManager
        FieldLayout mockLayout = mock(FieldLayout.class);
        when(mockLayout.getFieldLayoutItem(any(CustomField.class))).thenReturn(null);
        FieldLayoutManager fieldLayoutManager = mock(FieldLayoutManager.class);
        when(fieldLayoutManager.getFieldLayout(any(Issue.class))).thenReturn(mockLayout);

        CustomFieldTypeModuleDescriptor supportXMLDesriptor = mock(CustomFieldTypeModuleDescriptor.class);
        when(supportXMLDesriptor.isXMLTemplateExists()).thenReturn(true);
        when(supportXMLDesriptor.getViewXML(any(CustomField.class), eq(mockedIssue), any(FieldLayoutItem.class), eq(false))).thenReturn("a");
        when(supportXMLDesriptor.getViewXML(any(CustomField.class), eq((Issue) null), any(FieldLayoutItem.class), eq(false))).thenReturn(null);

        CustomFieldTypeModuleDescriptor unsupportXMLDesriptor = mock(CustomFieldTypeModuleDescriptor.class);
        when(unsupportXMLDesriptor.isXMLTemplateExists()).thenReturn(false);

        CustomFieldType supportXMLtype = mock(CustomFieldType.class);
        when(supportXMLtype.getDescriptor()).thenReturn(supportXMLDesriptor);
        CustomField mockFieldWithXML = mock(CustomField.class);
        when(mockFieldWithXML.getCustomFieldType()).thenReturn(supportXMLtype);

        CustomFieldType unsupportXMLtype = mock(CustomFieldType.class);
        when(unsupportXMLtype.getDescriptor()).thenReturn(unsupportXMLDesriptor);
        CustomField mockFieldWithoutXML = mock(CustomField.class);
        when(mockFieldWithoutXML.getCustomFieldType()).thenReturn(unsupportXMLtype);

        //initializing elements
        ExtendedIssueView extendedIssueView = new ExtendedIssueView(null, fieldLayoutManager, null, null, null);

        //verify tests
        assertEquals("a", extendedIssueView.getCustomFieldXML(mockFieldWithXML, mockedIssue));
        assertEquals("", extendedIssueView.getCustomFieldXML(mockFieldWithXML, null));
        assertEquals("", extendedIssueView.getCustomFieldXML(mockFieldWithoutXML, mockedIssue));

        verify(supportXMLDesriptor, times(2)).isXMLTemplateExists();
        verify(supportXMLDesriptor, times(2)).getViewXML(any(CustomField.class), any(Issue.class), any(FieldLayoutItem.class), anyBoolean());
        verify(unsupportXMLDesriptor, times(1)).isXMLTemplateExists();
    }

}
