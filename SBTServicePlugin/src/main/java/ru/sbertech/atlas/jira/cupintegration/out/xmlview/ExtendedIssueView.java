package ru.sbertech.atlas.jira.cupintegration.out.xmlview;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.datetime.DateTimeStyle;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.views.util.IssueViewUtil;
import com.atlassian.jira.issue.worklog.Worklog;
import com.atlassian.jira.issue.worklog.WorklogManager;
import com.atlassian.jira.plugin.customfield.CustomFieldTypeModuleDescriptor;
import com.atlassian.jira.plugin.issueview.AbstractIssueView;
import com.atlassian.jira.plugin.issueview.IssueViewRequestParams;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.usercompatibility.UserCompatibilityHelper;
import com.atlassian.jira.util.JiraVelocityUtils;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.atlassian.jira.web.bean.CustomIssueXMLViewFieldsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.tools.generic.MathTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbertech.atlas.jira.cupintegration.exception.RequiredComponentNotFoundException;
import ru.sbertech.atlas.jira.cupintegration.out.model.UserContainer;
import ru.sbertech.atlas.jira.cupintegration.out.model.WorkLogContainer;
import ru.sbertech.atlas.jira.userenrich.dto.UserInfoDTO;
import ru.sbertech.atlas.jira.userenrich.manager.UserInfoManager;

import java.util.*;

/**
 * Created by Yaroslav Astafiev on 23/11/2015.
 * Department of analytical solutions and system services improvement.
 */
public class ExtendedIssueView extends AbstractIssueView {
    private static final Logger log = LoggerFactory.getLogger(ExtendedIssueView.class);

    private final JiraAuthenticationContext authenticationContext;
    private final FieldLayoutManager fieldLayoutManager;
    private final IssueViewUtil issueViewUtil;
    private final DateTimeFormatterFactory dateTimeFormatterFactory;
    private final WorklogManager worklogManager;

    ExtendedIssueView(final JiraAuthenticationContext authenticationContext, final FieldLayoutManager fieldLayoutManager, final IssueViewUtil issueViewUtil,
                             final DateTimeFormatterFactory dateTimeFormatterFactory, final WorklogManager worklogManager) {
        this.authenticationContext = authenticationContext;
        this.fieldLayoutManager = fieldLayoutManager;
        this.issueViewUtil = issueViewUtil;
        this.dateTimeFormatterFactory = dateTimeFormatterFactory;
        this.worklogManager = worklogManager;
    }

    private static String getCrowdUserEmployeeCode(User user) throws RequiredComponentNotFoundException {
        if (user == null) {
            return null;
        }
        UserInfoManager userInfoManager = ComponentAccessor.getOSGiComponentInstanceOfType(UserInfoManager.class);
        if (userInfoManager == null) {
            throw new RequiredComponentNotFoundException(UserInfoManager.class);
        }
        UserInfoDTO userInfoDTO = userInfoManager.getUserInfoByCrowdUser(user);
        return userInfoDTO == null ? null : userInfoDTO.getEmployeeCode();
    }

    @Override
    public String getContent(final Issue issue, final IssueViewRequestParams issueViewRequestParams) {
        return getBody(issue, issueViewRequestParams);
    }

    @Override
    public String getBody(final Issue issue, final IssueViewRequestParams issueViewRequestParams) {
        Map<String, Object> bodyParams = getBodyParameters(issue, issueViewRequestParams);

        log.debug("[SINGLE ISSUE VIEW] perform velocity rendering for issue: " + issue);
        return descriptor.getHtml("view", bodyParams);
    }

    Map<String, Object> getBodyParameters(final Issue issue, final IssueViewRequestParams issueViewRequestParams){
        Map<String, Object> bodyParams = JiraVelocityUtils.getDefaultVelocityParams(authenticationContext);
        log.debug("[SINGLE ISSUE VIEW] generating body params for issue: " + issue);
        bodyParams.put("issue", issue);
        bodyParams.put("i18n", authenticationContext.getI18nHelper());
        bodyParams.put("dateTimeFormatter", dateTimeFormatterFactory.formatter().forLoggedInUser().withStyle(DateTimeStyle.ISO_8601_DATE_TIME));
        bodyParams.put("dateFormatter", dateTimeFormatterFactory.formatter().withSystemZone().withStyle(DateTimeStyle.ISO_8601_DATE));
        bodyParams.put("math", new MathTool());

        log.debug("[SINGLE ISSUE VIEW] adding list of XML view beans for custom fields for issue: " + issue);
        FieldVisibilityManager fieldVisibilityManager = ComponentAccessor.getComponent(FieldVisibilityManager.class);
        CustomIssueXMLViewFieldsBean customIssueXmlViewFieldsBean = new CustomIssueXMLViewFieldsBean(fieldVisibilityManager, issueViewRequestParams.getIssueViewFieldParams(),
                        issue.getProjectObject().getId(), issue.getIssueTypeObject().getId());

        log.debug("[SINGLE ISSUE VIEW] performing custom issue View template for issue: " + issue);
        bodyParams.put("issueXmlViewFields", customIssueXmlViewFieldsBean);

        bodyParams.put("xmlView", this);

        log.debug("[SINGLE ISSUE VIEW] setting user");
        final ApplicationUser user = authenticationContext.getUser();
        bodyParams.put("remoteUser", user);

        log.debug("[SINGLE ISSUE VIEW] generating worklogs for issue: " + issue);
        bodyParams.put("worklogs", getWorkLog(issue));

        log.debug("[SINGLE ISSUE VIEW] adding custom fields XMLs into velocity params for issue: " + issue);
        String issueTypeId = issue.getIssueTypeObject().getId();
        FieldLayout fieldLayout = fieldLayoutManager.getFieldLayout(issue);
        List customFields = fieldLayout.getVisibleCustomFieldLayoutItems(issue.getProjectObject(), Collections.singletonList(issueTypeId));
        bodyParams.put("visibleCustomFields", customFields);

        return  bodyParams;
    }

    public String getRenderedContent(String fieldName, String value, Issue issue) {
        return issueViewUtil.getRenderedContent(fieldName, value, issue);
    }

    String getPrettyDuration(Long v) {
        return issueViewUtil.getPrettyDuration(v);
    }

    public String getCustomFieldXML(CustomField field, Issue issue) {
        FieldLayoutItem fieldLayoutItem = fieldLayoutManager.getFieldLayout(issue).getFieldLayoutItem(field);

        // Only try to get the xml data if the custom field has a template defined for XML
        CustomFieldTypeModuleDescriptor moduleDescriptor = field.getCustomFieldType().getDescriptor();
        if (moduleDescriptor.isXMLTemplateExists()) {
            String xmlValue = moduleDescriptor.getViewXML(field, issue, fieldLayoutItem, false);
            // If the template generates a null value we don't want to return it and we want to alert the logs
            if (xmlValue != null) {
                log.debug("SUCCESS OPERATION: [CUSTOM FIELD XML] custom field XML for field " + field + " in issue " + issue + " generated");
                return xmlValue;
            } else {
                log.info("[CUSTOM FIELD XML] No XML data has been defined for the custom field [" + field.getId() + "]");
            }
        }
        log.debug("SUCCESS OPERATION: [CUSTOM FIELD XML] custom field XML for field " + field + " in issue " + issue + " doesn't provided");
        return "";
    }

    private Map<UserContainer, List<WorkLogContainer>> getWorkLog(Issue issue) {
        log.debug("PERFORM OPERATION: [WORK LOG PROCESSING] generating worklogs for issue: " + issue);
        Map<UserContainer, List<WorkLogContainer>> container = new HashMap<>();
        List<Worklog> workLogs = worklogManager.getByIssue(issue);
        List<WorkLogContainer> userWorkLog;
        for (Worklog wLog : workLogs) {
            try {
                final ApplicationUser authorObject = wLog.getAuthorObject();
                if (authorObject == null) {
                    log.error("UNEXPECTED BEHAVIOUR: [WORK LOG PROCESSING] AuthorObject variable is null for worklog in ISSUE: " + issue.getKey() + " worklog will be ignored.");
                    continue;
                }
                User directoryUser = UserCompatibilityHelper.convertUserObject(authorObject).getUser();
                log.debug("PERFORM OPERATION: [WORK LOG PROCESSING] Starting to get Employee Code for user " + directoryUser);
                /*Could throw RequiredComponentException if UserInfoDTO is null
                * Such Users should be ignored on time sheets export
                * */
                String employeeCode = getCrowdUserEmployeeCode(directoryUser);
                if (StringUtils.isEmpty(employeeCode)) {
                    log.error("[WORK LOG PROCESSING] Couldn't get user employee code from ActiveDirectory for user " + directoryUser + " worklog will be ignored.");
                    continue;
                }
                UserContainer user = new UserContainer(authorObject, employeeCode);
                log.debug("SUCCESS OPERATION: [WORK LOG PROCESSING] User Employee Code gotten successfully for user " + directoryUser);

                final WorkLogContainer workLogContainer = new WorkLogContainer(wLog.getStartDate(), wLog.getTimeSpent());
                if (container.containsKey(user)) {
                    log.debug("[WORK LOG PROCESSING] Updating worklog for user " + directoryUser);
                    userWorkLog = container.get(user);
                    userWorkLog.add(workLogContainer);
                    container.put(user, userWorkLog);
                } else {
                    log.debug("[WORK LOG PROCESSING] Adding new user: " + directoryUser + " into worklog container");
                    container.put(user, new ArrayList<WorkLogContainer>() {{
                        add(workLogContainer);
                    }});
                }
            } catch (RequiredComponentNotFoundException runtime) {
                log.error("UNEXPECTED BEHAVIOUR: [WORK LOG PROCESSING] OSGi container doesn't contain required components.", runtime);
                throw new RuntimeException(runtime);
            } catch (Exception e) {
                if (wLog == null) {
                    log.error("UNEXPECTED BEHAVIOUR: [WORK LOG PROCESSING] work log can't be null. Problematic issue: " + issue, e);
                } else {
                    log.error("UNEXPECTED BEHAVIOUR: [WORK LOG PROCESSING]. Can't parse worklog for issue: " + wLog.getIssue() + " with user key: " + wLog.getAuthorKey(), e);
                }
            }
        }
        log.debug("SUCCESS OPERATION: [WORK LOG PROCESSING] generated worklogs for issue: " + issue);
        return container;
    }
}
