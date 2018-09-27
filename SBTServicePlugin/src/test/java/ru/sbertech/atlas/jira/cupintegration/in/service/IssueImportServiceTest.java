package ru.sbertech.atlas.jira.cupintegration.in.service;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueInputParametersImpl;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserUtils;
import com.atlassian.jira.user.util.DefaultUserManager;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import org.apache.commons.lang.NullArgumentException;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.in.ParamsEnricher;
import ru.sbertech.atlas.jira.cupintegration.in.issue.CreateStrategy;
import ru.sbertech.atlas.jira.cupintegration.in.issue.UpdateStrategy;
import ru.sbertech.atlas.jira.cupintegration.in.model.IMapping;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;
import ru.sbertech.atlas.jira.cupintegration.in.repository.IMappingRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by Sedelnikov FM on 21/01/2016.
 * Department of analytical solutions and system services improvement.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IssueImportService.class, UserUtils.class})
public class IssueImportServiceTest {

    @Test
    public void testBuildIssueInputParameters_ValidParamsMap_ValidIssueInputParams() throws Exception {

        Map<String, String> params = new HashMap<>();
        params.put("ppm_rfc_name", "test-summary");
        params.put("ppm_rfc_area_ps", "test-project");

        ApplicationUser user = mock(ApplicationUser.class);
        IssueService issueService = mock(IssueService.class);
        when(issueService.newIssueInputParameters()).thenReturn(new IssueInputParametersImpl(null));

        IMapping mapping1 = mock(IMapping.class);
        when(mapping1.getXmlId()).thenReturn("ppm_rfc_name");
        when(mapping1.getFieldId()).thenReturn("summary");
        when(mapping1.getFieldType()).thenReturn("Default field");
        IMapping mapping2 = mock(IMapping.class);
        when(mapping2.getXmlId()).thenReturn("ppm_rfc_area_ps");
        when(mapping2.getFieldId()).thenReturn("project");
        when(mapping2.getFieldType()).thenReturn("Default field");
        Map<String, IMapping> mappingMap = new HashMap<>();
        mappingMap.put(mapping1.getXmlId(), mapping1);
        mappingMap.put(mapping2.getXmlId(), mapping2);
        IMappingRepository mappingRepository = mock(IMappingRepository.class);
        when(mappingRepository.getMappingsMap()).thenReturn(mappingMap);

        ParamsEnricher enricher = mock(ParamsEnricher.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                IssueInputParameters inputParameters = (IssueInputParameters) invocationOnMock.getArguments()[1];
                inputParameters.setProjectId(1L);
                inputParameters.setSummary("test-summary");
                return null;
            }
        }).when(enricher).enrichIssueDefaultFields(anyMap(), Matchers.<IssueInputParameters>anyObject(), Matchers.<ApplicationUser>any(), anyString());

        IssueImportService issueImportService = new IssueImportService(mappingRepository, enricher, issueService, null, null);
        IssueInputParameters resultIssueInputParameters = issueImportService.buildIssueInputParameters(params, "10000", user);

        assertEquals(1L, (long) resultIssueInputParameters.getProjectId());
        assertEquals("test-summary", resultIssueInputParameters.getSummary());
    }

    @Test
    public void testProcess_validParamsMap_importResultWithCreatedIssueKey() throws Exception {

        Map<String, String> params = new HashMap<>();
        params.put("ppm_rfc_id", "1");
        ImportSettings importSettings = mock(ImportSettings.class);
        importSettings.cupZniIdField = "10000";
        importSettings.cupKrpIdField = "10100";
        importSettings.userName = "admin";
        ImportSettingsServiceJiraStorage importSettingsServiceJiraStorage = mock(ImportSettingsServiceJiraStorage.class);
        when(importSettingsServiceJiraStorage.getImportSetting()).thenReturn(importSettings);

        ApplicationUser user = mock(ApplicationUser.class);
        PowerMockito.mockStatic(UserUtils.class);
        when(UserUtils.getUser(importSettings.userName)).thenReturn(user);
        new MockComponentWorker().addMock(ComponentAccessor.class, new ComponentAccessor()).init();

        SearchProvider searchProvider = mock(SearchProvider.class);
        SearchResults searchResults = mock(SearchResults.class);
        @SuppressWarnings("unchecked")
        List<Issue> issues = mock(List.class);
        when(issues.size()).thenReturn(0);
        when(searchResults.getIssues()).thenReturn(issues);
        when(searchProvider.searchOverrideSecurity(any(Query.class), any(ApplicationUser.class), any(PagerFilter.class), any(org.apache.lucene.search.Query.class))).thenReturn(searchResults);

        IssueImportService issueImportService = spy(new IssueImportService(null, null, null, importSettingsServiceJiraStorage, searchProvider));
        IssueInputParameters issueInputParameters = mock(IssueInputParameters.class);
        doReturn(issueInputParameters).when(issueImportService).buildIssueInputParameters(params, "10000", user);
        CreateStrategy createIssueStrategy = mock(CreateStrategy.class);
        when(createIssueStrategy.importIssue(user, issueInputParameters, null)).thenReturn(new ImportResult(ResultType.ISSUE, ResultState.CREATED, "TEST-1"));
        PowerMockito.whenNew(CreateStrategy.class).withAnyArguments().thenReturn(createIssueStrategy);
        ImportResult importResult = issueImportService.importObject(params);

        assertEquals(ResultType.ISSUE, importResult.getType());
        assertEquals(ResultState.CREATED, importResult.getState());
        assertEquals("TEST-1", importResult.getValue());

    }

    @Test
    public void testImportObject_validParamsMap_importResultWithUpdatedIssueKey() throws Exception {

        Map<String, String> params = new HashMap<>();
        params.put("ppm_rfc_id", "1");
        ImportSettings importSettings = mock(ImportSettings.class);
        importSettings.cupZniIdField = "10000";
        importSettings.cupKrpIdField = "10100";
        importSettings.userName = "admin";
        ImportSettingsServiceJiraStorage importSettingsServiceJiraStorage = mock(ImportSettingsServiceJiraStorage.class);
        when(importSettingsServiceJiraStorage.getImportSetting()).thenReturn(importSettings);

        ApplicationUser user = mock(ApplicationUser.class);
        PowerMockito.mockStatic(UserUtils.class);
        when(UserUtils.getUser(importSettings.userName)).thenReturn(user);
        new MockComponentWorker().addMock(ComponentAccessor.class, new ComponentAccessor()).init();

        SearchProvider searchProvider = mock(SearchProvider.class);
        SearchResults searchResults = mock(SearchResults.class);
        MockIssue mockIssue = new MockIssue();
        mockIssue.setKey("TEST-1");
        List<Issue> issues = new ArrayList<>();
        issues.add(mockIssue);
        when(searchResults.getIssues()).thenReturn(issues);
        when(searchProvider.searchOverrideSecurity(any(Query.class), any(ApplicationUser.class), any(PagerFilter.class), any(org.apache.lucene.search.Query.class))).thenReturn(searchResults);

        IssueImportService issueImportService = spy(new IssueImportService(null, null, null, importSettingsServiceJiraStorage, searchProvider));
        IssueInputParameters issueInputParameters = mock(IssueInputParameters.class);
        doReturn(issueInputParameters).when(issueImportService).buildIssueInputParameters(params, "10000", user);
        UpdateStrategy updateIssueStrategy = mock(UpdateStrategy.class);
        when(updateIssueStrategy.importIssue(user, issueInputParameters, null)).thenReturn(new ImportResult(ResultType.ISSUE, ResultState.UPDATED, "TEST-1"));
        PowerMockito.whenNew(UpdateStrategy.class).withAnyArguments().thenReturn(updateIssueStrategy);
        ImportResult importResult = issueImportService.importObject(params);

        assertEquals(ResultType.ISSUE, importResult.getType());
        assertEquals(ResultState.UPDATED, importResult.getState());
        assertEquals("TEST-1", importResult.getValue());
    }

    @Test
    public void testImportObject_nullParamsMap_NullArgumentException() throws Exception {
        IssueImportService issueImportService = new IssueImportService(null, null, null, null, null);

        try {
            issueImportService.importObject(null);
        } catch (Exception e) {
            assertThat(e, new IsInstanceOf(NullArgumentException.class));
            assertEquals("Argument must not be null.", e.getMessage());
        }
    }

    @Test
    public void testImportObject_validParamsMap_ImportResultWithError() throws Exception {

        Map<String, String> params = new HashMap<>();
        params.put("ppm_rfc_id", "12345");
        params.put("ppm_object_type", "ЗНИ");
        ImportSettingsServiceJiraStorage importSettingsServiceJiraStorage = mock(ImportSettingsServiceJiraStorage.class);
        ImportSettings importSettings = mock(ImportSettings.class);
        importSettings.cupZniIdField = "10000";
        importSettings.cupKrpIdField = "10100";
        importSettings.userName = "admin";
        when(importSettingsServiceJiraStorage.getImportSetting()).thenReturn(importSettings);

        ApplicationUser user = mock(ApplicationUser.class);
        PowerMockito.mockStatic(UserUtils.class);
        when(UserUtils.getUser(importSettings.userName)).thenReturn(user);
        new MockComponentWorker().addMock(ComponentAccessor.class, new ComponentAccessor()).init();

        SearchProvider searchProvider = mock(SearchProvider.class);
        SearchResults searchResults = mock(SearchResults.class);
        @SuppressWarnings("unchecked")
        List<Issue> issues = mock(List.class);
        when(issues.size()).thenReturn(2);
        when(searchResults.getIssues()).thenReturn(issues);
        when(searchProvider.searchOverrideSecurity(any(Query.class), any(ApplicationUser.class), any(PagerFilter.class), any(org.apache.lucene.search.Query.class))).thenReturn(searchResults);

        IssueImportService issueImportService = new IssueImportService(null, null, null, importSettingsServiceJiraStorage, searchProvider);

        ImportResult importResult = issueImportService.importObject(params);
        assertEquals(importResult.getState(), ResultState.ERROR);
        assertEquals(importResult.getType(), ResultType.ISSUE);
        assertEquals(importResult.getValue(), "More then one Epic with the same ZNI or KRP id");
    }

}
