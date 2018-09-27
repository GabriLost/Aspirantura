package ru.sbertech.atlas.jira.cupintegration.out;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.search.ClauseTooComplexSearchException;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.views.util.SearchRequestViewUtils;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.plugin.issueview.IssueViewFieldParams;
import com.atlassian.jira.plugin.issueview.IssueViewRequestParamsHelper;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestParams;
import com.atlassian.jira.plugin.searchrequestview.SearchRequestView;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import ru.sbertech.atlas.jira.cupintegration.exception.RequiredComponentNotFoundException;
import ru.sbertech.atlas.jira.cupintegration.out.async.ExportJobRunner;
import ru.sbertech.atlas.jira.cupintegration.out.model.PluginSettingsHolder;
import ru.sbertech.atlas.jira.cupintegration.out.service.PluginSettingsService;
import ru.sbertech.atlas.jira.cupintegration.out.utils.HttpUtil;
import ru.sbertech.atlas.jira.cupintegration.out.utils.TimesheetGeneratorUtils;
import ru.sbertech.atlas.jira.cupintegration.out.xmlview.ExtendedSearchRequestParams;
import ru.sbertech.atlas.jira.cupintegration.out.xmlview.ExtendedSearchRequestView;

import java.io.*;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TimesheetGenerator implements InitializingBean, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(TimesheetGenerator.class);

    private static final String JOB_NAME = "TimesheetGenerator.AutoExportJob";
    private static final String JOB_RUNNER_KEY = "TimesheetGenerator.RunnerKey";
    private static final String EXPORT_DATE_FORMAT = "yyyyMMdd_HHmm";

    private final IssueViewRequestParamsHelper issueViewRequestParamsHelper;
    private final PluginSettingsService pluginSettingsService;
    private final ExecutorService executorService;
    private final SchedulerService schedulerService;

    public TimesheetGenerator(IssueViewRequestParamsHelper issueViewRequestParamsHelper, PluginSettingsService pluginSettingsService) {
        this.issueViewRequestParamsHelper = issueViewRequestParamsHelper;
        this.pluginSettingsService = pluginSettingsService;
        this.executorService = Executors.newSingleThreadExecutor();
        schedulerService = ComponentAccessor.getComponent(SchedulerService.class);
    }

    public Future<String> buildXMLAsync(final String fromDate, final String toDate, final String filter) throws RequiredComponentNotFoundException {
        new TimesheetGeneratorChecker().checkUserInfoManager().checkLocalDirectory(pluginSettingsService.getPluginSettings().sharedFolder);

        log.debug(String.format("Scheduling exporting with params fromDate=[%s] toDate=[%s] filter=[%s]", fromDate, toDate, filter));

        final JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();
        final ApplicationUser originalUser = jiraAuthenticationContext.getUser();

        return executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                jiraAuthenticationContext.setLoggedInUser(originalUser);
                return buildXML(fromDate, toDate, filter);
            }
        });
    }

    private String buildXML(String fromDate, String toDate, String filter) {
        try {
            return getXMLByJQL(TimesheetGeneratorUtils.buildQueryFromFilterAndDates(fromDate, toDate, filter));
        } catch (JqlParseException e) {
            log.error("Error while JQL parsing", e);
        } finally {
            log.info(String.format("Finished exporting with params fromDate=[%s] toDate=[%s] filter=[%s]", fromDate, toDate, filter));
        }
        return null;
    }

    public void generateXmlAndStore(final String fromDate, final String toDate, final String filter, final ApplicationUser user) throws RequiredComponentNotFoundException {
        new TimesheetGeneratorChecker().checkUserInfoManager().checkLocalDirectory(pluginSettingsService.getPluginSettings().sharedFolder);

        final JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                //ToDo узнать как создать юзера для Job
                jiraAuthenticationContext.setLoggedInUser(null);
                try {
                    String fileName = StringUtils.EMPTY;
                    final PluginSettingsHolder pluginSettings = pluginSettingsService.getPluginSettings();
                    if (!StringUtils.isEmpty(pluginSettings.sharedFolder)) {
                        fileName = pluginSettings.sharedFolder + File.separator;
                    }
                    fileName += StringUtils.trimToEmpty(pluginSettings.filePrefix) + DateFormatUtils.format(new Date(), EXPORT_DATE_FORMAT) + ".xml";
                    log.info(String.format("Starting exporting with params fromDate=[%s] toDate=[%s] filter=[%s] fileName=[%s]", fromDate, toDate, filter, fileName));
                    String xml = buildXML(fromDate, toDate, filter);
                    if (xml != null) {
                        FileUtils.writeStringToFile(new File(fileName), xml);
                        log.info(String.format("Finished exporting with params fromDate=[%s] toDate=[%s] filter=[%s] fileName=[%s]", fromDate, toDate, filter, fileName));
                        if (pluginSettings.exportSmbWebService == null) {
                            return;
                        }

                        Map<String, String> body = new HashMap<>(5);
                        body.put("fileName", fileName);
                        body.put("userName", pluginSettings.exportSmbLogin);
                        body.put("userPassword", pluginSettings.exportSmbPassword);
                        body.put("networkFolder", pluginSettings.exportPathSmb);
                        body.put("resultServiceUrl", pluginSettings.resultServiceUrl);
                        HttpUtil.sendPostRequest(pluginSettings.exportSmbWebService, body);
                    }
                } catch (IOException e) {
                    log.error("I/O error while writing auto-export file", e);
                }
            }
        });
    }

    private String getXMLByJQL(Query jql) {
        final IssueViewFieldParams issueViewFieldParams = issueViewRequestParamsHelper.getIssueViewFieldParams(Collections.emptyMap());
        SearchRequestParams searchRequestParams = new ExtendedSearchRequestParams(PagerFilter.getUnlimitedFilter(), issueViewFieldParams);

        log.info("Build export for JQL: " + jql.toString());

        SearchRequestView view = SearchRequestViewUtils.getSearchRequestView(ExtendedSearchRequestView.class);
        if (view == null) {
            throw new RuntimeException(new RequiredComponentNotFoundException(ExtendedSearchRequestView.class));
        }
        try (final Writer writer = new StringWriter()) {
            try {
                view.writeSearchResults(new SearchRequest(jql), searchRequestParams, writer);
            } catch (ClauseTooComplexSearchException tooComplex) {
                log.error("JQL request is too complex", tooComplex);
            } catch (final SearchException searchError) {
                log.error("JIRA search request is not enabled now", searchError);
                throw new RuntimeException(searchError);
            }
            writer.flush();
            return writer.toString();
        } catch (IOException e) {
            log.error("Unexpected behaviour: StringWriter doesn't work, probably jvm-heap is down", e);
            throw new RuntimeException(e);
        }
    }

    public void reschedulePeriodicExport(PluginSettingsHolder localSettings) throws ParseException, SchedulerServiceException {

        if (localSettings == null) {
            localSettings = pluginSettingsService.getPluginSettings();
        }

        try {
            log.info("try to unscheduling job");
            schedulerService.unscheduleJob(JobId.of(JOB_NAME));
        } catch (Exception ignored) {/*Atlassian is very lazy. They didn't implement rescheduling, so we delete job and ignores errors if its not exists*/}

        if (Boolean.valueOf(localSettings.autoExport)) {

            Map<String, Serializable> param = new HashMap<>();
            param.put(ExportJobRunner.PLUGIN_SETTING_KEY, localSettings);
            JobConfig jobConfig = JobConfig.forJobRunnerKey(JobRunnerKey.of(JOB_RUNNER_KEY)).withParameters(param).withSchedule(Schedule.forCronExpression(localSettings.schedule))
                .withRunMode(RunMode.RUN_LOCALLY);
            schedulerService.scheduleJob(JobId.of(JOB_NAME), jobConfig);
            log.info("Scheduled job for auto-export timesheets with " + localSettings.toString());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        schedulerService.registerJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY), new ExportJobRunner(this));
        log.info("Job runner with key " + JOB_RUNNER_KEY + " registered");
        this.reschedulePeriodicExport(null);
    }

    @Override
    public void destroy() {
        try {
            schedulerService.unscheduleJob(JobId.of(JOB_NAME));
            log.info("Job with name " + JOB_NAME + " unscheduled");
            schedulerService.unregisterJobRunner(JobRunnerKey.of(JOB_RUNNER_KEY));
            log.info("Job runner with key " + JOB_RUNNER_KEY + " unregistered");
        } catch (Exception e) {
            log.error("Try to unscheduleJob with name: " + JOB_NAME + " and unregisterJobRunner with key: " + JOB_RUNNER_KEY, e);
        }
    }
}
