package ru.sbertech.atlas.jira.cupintegration.in.service;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.parser.DefaultJqlQueryParser;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.UserUtils;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import ru.sbertech.atlas.jira.cupintegration.in.ParamsEnricher;
import ru.sbertech.atlas.jira.cupintegration.in.issue.AbstractImportStrategy;
import ru.sbertech.atlas.jira.cupintegration.in.issue.CreateStrategy;
import ru.sbertech.atlas.jira.cupintegration.in.issue.UpdateStrategy;
import ru.sbertech.atlas.jira.cupintegration.in.model.IMapping;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;
import ru.sbertech.atlas.jira.cupintegration.in.repository.IMappingRepository;

import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static ru.sbertech.atlas.jira.cupintegration.in.utils.JiraFieldsParseUtils.splitParamMap;

/**
 * Created by Sedelnikov FM on 24/12/2015.
 */
public class IssueImportService implements IImportProcessor {

    public static final String TASK = "Задача КРП";
    public static final String ZNI = "ЗНИ";
    private static final Logger LOG = Logger.getLogger(IssueImportService.class);
    private static final String EPIC_ISSUE_TYPE = "10000";
    private static final String GENERIC_ISSUE_TYPE = "10746";

    private static final String CUP_ZNI_ID = "ppm_rfc_id";
    private static final String CUP_KRP_ID = "ppm_task_id";
    private static final String CUP_KRP_ZNI_ID = "ppm_task_rfc_id";

    private static final Map<String, String> jiraIssueTypes = new HashMap<>();

    private final IMappingRepository mappingRepository;
    private final ParamsEnricher paramsEnricher;
    private final IssueService issueService;
    private final ImportSettingsServiceJiraStorage importSettingsServiceJiraStorage;
    private final SearchProvider searchProvider;

    public IssueImportService(IMappingRepository mappingRepository, ParamsEnricher paramsEnricher, IssueService issueService,
        ImportSettingsServiceJiraStorage importSettingsServiceJiraStorage, SearchProvider searchProvider) {
        this.mappingRepository = mappingRepository;
        this.paramsEnricher = paramsEnricher;
        this.issueService = issueService;
        this.importSettingsServiceJiraStorage = importSettingsServiceJiraStorage;
        this.searchProvider = searchProvider;

        jiraIssueTypes.put(EPIC_ISSUE_TYPE, "Epic");
        jiraIssueTypes.put(GENERIC_ISSUE_TYPE, "Generic");
    }

    /**
     * @param params Map with tags and values from xml from CUP
     * @return Created/Updated issue or String with errors
     * @throws Exception
     */
    public ImportResult importObject(Map<String, String> params) {
        try {
            if (MapUtils.isEmpty(params)) {
                return new ImportResult(ResultType.UNDEFINED, ResultState.ERROR, "Parameters map is empty");
            }

            //ToDo: получать провалидированные настройки в конструкторе
            ImportSettings importSettings = importSettingsServiceJiraStorage.getImportSetting();
            String zniCupFieldId = importSettings.cupZniIdField;
            String krpCupFieldId = importSettings.cupKrpIdField;

            String zniId = params.get(CUP_ZNI_ID) == null ? params.get(CUP_KRP_ZNI_ID) : params.get(CUP_ZNI_ID);
            String issueType = isEmpty(zniId) ? GENERIC_ISSUE_TYPE : EPIC_ISSUE_TYPE;

            String queryString = "IssueType=" + issueType;

            if (issueType == EPIC_ISSUE_TYPE) {
                queryString += " AND cf[" + zniCupFieldId + "]" + "~" + zniId;
            } else {
                queryString += " AND cf[" + krpCupFieldId + "]" + "~" + params.get(CUP_KRP_ID);
            }

            Query query = new DefaultJqlQueryParser().parseQuery(queryString);

            ApplicationUser user = UserUtils.getUser(importSettings.userName);
            SearchResults searchResults = this.searchProvider.searchOverrideSecurity(query, user, PagerFilter.getUnlimitedFilter(), null);

            if (searchResults.getIssues().size() > 1) {
                return new ImportResult(ResultType.ISSUE, ResultState.ERROR, String.format("More then one %s with the same ZNI or KRP id", jiraIssueTypes.get(issueType)));
            }

            IssueInputParameters issueInputParameters = buildIssueInputParameters(params, issueType, user);

            AbstractImportStrategy importIssueStrategy;
            Long issueId = null;

            if (searchResults.getIssues().size() == 0) {
                importIssueStrategy = new CreateStrategy(issueService);
            } else {
                importIssueStrategy = new UpdateStrategy(issueService);
                issueId = searchResults.getIssues().get(0).getId();
            }

            return importIssueStrategy.importIssue(user, issueInputParameters, issueId);
        } catch (Exception e) {
            LOG.error("IssueImportService couldn't procces.", e);
            return new ImportResult(ResultType.ISSUE, ResultState.ERROR, e.getMessage());
        }
    }

    IssueInputParameters buildIssueInputParameters(Map<String, String> params, String issueType, ApplicationUser user) {
        IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();

        Map<String, String> defaultFields = new HashMap<>();
        Map<String, String> customFields = new HashMap<>();

        Map<String, IMapping> mappingMap = mappingRepository.getMappingsMap();

        splitParamMap(params, defaultFields, customFields, mappingMap);

        LOG.debug("Enrich defaultFields " + defaultFields.toString());
        paramsEnricher.enrichIssueDefaultFields(defaultFields, issueInputParameters, user, issueType);
        LOG.debug("Enrich customFields " + customFields.toString());
        paramsEnricher.enrichIssueCustomFields(customFields, issueInputParameters);
        return issueInputParameters;
    }

}
