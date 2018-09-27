package ru.sbertech.atlas.jira.cupintegration.out.xmlview;

import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.views.SingleIssueWriter;
import com.atlassian.jira.issue.views.util.SearchRequestViewUtils;
import com.atlassian.jira.plugin.issueview.AbstractIssueView;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestParams;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestViewModuleDescriptor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.JiraVelocityUtils;
import com.atlassian.jira.web.bean.PagerFilter;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.service.ImportSettingService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by Yaroslav Astafiev on 17/03/2016.
 * Department of analytical solutions and system services improvement.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ExtendedSearchRequestView.class, SearchRequestViewUtils.class, JiraVelocityUtils.class, File.class, FileWriter.class})
public class ExtendedSearchRequestViewTest {

    private JiraAuthenticationContext jiraAuthenticationContext;
    private ExtendedSearchRequestViewBodyWriterUtil searchRequestViewBodyWriterUtil;
    private ImportSettingService importSettingService;

    @Before
    public void init() {
        jiraAuthenticationContext = mock(JiraAuthenticationContext.class);
        searchRequestViewBodyWriterUtil = mock(ExtendedSearchRequestViewBodyWriterUtil.class);
        importSettingService = mock(ImportSettingService.class);
        ImportSettings importSettings = new ImportSettings();
        importSettings.cupKrpIdField = "12907";
        when(importSettingService.getImportSetting()).thenReturn(importSettings);
    }

    @Test
    public void testInitModuleDescriptor() throws Exception {
        SearchRequestViewModuleDescriptor moduleDescriptor = mock(SearchRequestViewModuleDescriptor.class);

        ExtendedSearchRequestView extendedSearchRequestView = new ExtendedSearchRequestView(jiraAuthenticationContext, searchRequestViewBodyWriterUtil, importSettingService);
        extendedSearchRequestView.init(moduleDescriptor);

        assertEquals(moduleDescriptor, Whitebox.getInternalState(extendedSearchRequestView, "moduleDescriptor"));
    }

    @Test
    public void testInitModuleDescriptor_Null() throws Exception {
        ExtendedSearchRequestView extendedSearchRequestView = new ExtendedSearchRequestView(jiraAuthenticationContext, searchRequestViewBodyWriterUtil, importSettingService);

        assertNull(Whitebox.getInternalState(extendedSearchRequestView, "moduleDescriptor"));
    }

    @Test
    public void testInitModuleDescriptor_Double_Init() throws Exception {
        SearchRequestViewModuleDescriptor moduleDescriptor = mock(SearchRequestViewModuleDescriptor.class);
        SearchRequestViewModuleDescriptor moduleDescriptor2 = mock(SearchRequestViewModuleDescriptor.class);

        ExtendedSearchRequestView extendedSearchRequestView = new ExtendedSearchRequestView(jiraAuthenticationContext, searchRequestViewBodyWriterUtil, importSettingService);
        extendedSearchRequestView.init(moduleDescriptor);
        extendedSearchRequestView.init(moduleDescriptor2);

        assertEquals(moduleDescriptor2, Whitebox.getInternalState(extendedSearchRequestView, "moduleDescriptor"));
    }

    //TODO: rewrite EXPECTED EXCEPTION on THROWN
    @Test(expected = RuntimeException.class)
    public void testWriteSearchResults_IssueViewNotDefined() throws Exception {
        PowerMockito.mockStatic(SearchRequestViewUtils.class);
        when(SearchRequestViewUtils.getIssueView(ExtendedIssueView.class)).thenReturn(null);

        ExtendedSearchRequestView extendedSearchRequestView = new ExtendedSearchRequestView(jiraAuthenticationContext, searchRequestViewBodyWriterUtil, importSettingService);
        extendedSearchRequestView.writeSearchResults(null, null, null);
    }

    @Test
    public void testWriteSearchResults_IssueViewTest() throws Exception {
        SearchRequestViewModuleDescriptor moduleDescriptor = mock(SearchRequestViewModuleDescriptor.class);

        when(moduleDescriptor.getHtml(eq("header"), any(Map.class))).thenReturn("<rss><channel><build_date>12-03-2016</build_date>");
        when(moduleDescriptor.getHtml(eq("footer"), any(Map.class))).thenReturn("</channel></rss>");
        PowerMockito.mockStatic(JiraVelocityUtils.class);
        when(JiraVelocityUtils.getDefaultVelocityParams(jiraAuthenticationContext)).thenReturn(new HashMap<String, Object>());
        ExtendedIssueView issueView = mock(ExtendedIssueView.class);
        PowerMockito.mockStatic(SearchRequestViewUtils.class);
        when(SearchRequestViewUtils.getIssueView(ExtendedIssueView.class)).thenReturn(issueView);
        File tempFile = File.createTempFile("Non-transformed", "");
        final FileWriter fw = new FileWriter(tempFile);
        PowerMockito.whenNew(FileWriter.class).withArguments(tempFile).thenReturn(fw);
        File transformedFile = File.createTempFile("Transformed", "");
        PowerMockito.mockStatic(File.class);
        when(File.createTempFile("Non-transformed", "")).thenReturn(tempFile);
        when(File.createTempFile("Transformed", "")).thenReturn(transformedFile);
        try(Writer writer = new StringWriter(); InputStream expected = getClass().getClassLoader().getResourceAsStream("expectedTimesheet.xml")) {
            SearchRequestParams searchRequestParams = mock(SearchRequestParams.class);
            when(searchRequestParams.getPagerFilter()).thenReturn(null);

            doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    try(InputStream is = getClass().getClassLoader().getResourceAsStream("singleTicket.xml")){
                        IOUtils.copy(is, fw);
                        return null;
                    }
                }
            }).when(searchRequestViewBodyWriterUtil).writeBody(any(Writer.class), any(AbstractIssueView.class), any(SearchRequest.class), any(SingleIssueWriter.class), any(PagerFilter.class));

            //start TEST
            ExtendedSearchRequestView extendedSearchRequestView =
                PowerMockito.spy(new ExtendedSearchRequestView(jiraAuthenticationContext, searchRequestViewBodyWriterUtil, importSettingService));
            extendedSearchRequestView.init(moduleDescriptor);
            extendedSearchRequestView.writeSearchResults(null, searchRequestParams, writer);

            //verify TEST
            verify(searchRequestViewBodyWriterUtil, times(1)).writeBody(any(Writer.class), any(AbstractIssueView.class), any(SearchRequest.class), any(SingleIssueWriter.class), any(PagerFilter.class));
            XMLUnit.setIgnoreWhitespace(true);
            assertXMLEqual(IOUtils.toString(expected, StandardCharsets.UTF_8), writer.toString());
        } finally {
            tempFile.deleteOnExit();
            transformedFile.deleteOnExit();
        }

    }

    //TODO: rewrite EXPECTED EXCEPTION on THROWN
    @Test(expected = DataAccessException.class)
    public void testWriteSearchResults_IssueViewThrows() throws Exception {
        SearchRequestViewModuleDescriptor moduleDescriptor = mock(SearchRequestViewModuleDescriptor.class);
        when(moduleDescriptor.getHtml(eq("header"), any(Map.class))).thenReturn("<rss><channel><build_date>12-03-2016</build_date>");

        PowerMockito.mockStatic(JiraVelocityUtils.class);
        when(JiraVelocityUtils.getDefaultVelocityParams(jiraAuthenticationContext)).thenReturn(new HashMap<String, Object>());

        ExtendedIssueView issueView = mock(ExtendedIssueView.class);
        PowerMockito.mockStatic(SearchRequestViewUtils.class);
        when(SearchRequestViewUtils.getIssueView(ExtendedIssueView.class)).thenReturn(issueView);

        File tempFile = File.createTempFile("Non-transformed", "");
        PowerMockito.mockStatic(File.class);
        when(File.createTempFile("Non-transformed", "")).thenReturn(tempFile);

        try(Writer writer = new StringWriter()) {
            SearchRequestParams searchRequestParams = mock(SearchRequestParams.class);
            when(searchRequestParams.getPagerFilter()).thenReturn(null);

            doThrow(SearchException.class).when(searchRequestViewBodyWriterUtil).searchCount(any(SearchRequest.class));
            doThrow(IOException.class).when(searchRequestViewBodyWriterUtil).writeBody(any(Writer.class), any(AbstractIssueView.class), any(SearchRequest.class), any(SingleIssueWriter.class), any(PagerFilter.class));

            ExtendedSearchRequestView extendedSearchRequestView =
                PowerMockito.spy(new ExtendedSearchRequestView(jiraAuthenticationContext, searchRequestViewBodyWriterUtil, importSettingService));
            extendedSearchRequestView.init(moduleDescriptor);
            extendedSearchRequestView.writeSearchResults(null, searchRequestParams, writer);
        } finally {
            tempFile.deleteOnExit();
        }

    }
}
