package ru.sbertech.atlas.jira.cupintegration.out.async;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbertech.atlas.jira.cupintegration.out.TimesheetGenerator;
import ru.sbertech.atlas.jira.cupintegration.out.model.PluginSettingsHolder;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExportJobRunner implements JobRunner {

    public static final String PLUGIN_SETTING_KEY = "ru.sbertech.atlas.jira.cupintegration.out.async.ExportJobRunner.pluginSettings";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static Logger log = LoggerFactory.getLogger(ExportJobRunner.class);
    private final TimesheetGenerator executor;

    public ExportJobRunner(TimesheetGenerator executor) {
        this.executor = executor;
    }

    @Nullable
    @Override
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {

        try {
            final PluginSettingsHolder pluginSettings = (PluginSettingsHolder) jobRunnerRequest.getJobConfig().getParameters().get(PLUGIN_SETTING_KEY);
            final ApplicationUser user = new ApplicationUser() {
                @Override
                public Long getId() {
                    return null;
                }

                @Override
                public String getKey() {
                    return null;
                }

                @Override
                public String getUsername() {
                    return null;
                }

                @Override
                public String getName() {
                    return null;
                }

                @Override
                public long getDirectoryId() {
                    return 0;
                }

                @Override
                public boolean isActive() {
                    return false;
                }

                @Override
                public String getEmailAddress() {
                    return null;
                }

                @Override
                public String getDisplayName() {
                    return null;
                }

                @Override
                public User getDirectoryUser() {
                    return null;
                }
            };

            Date to = new Date();
            Date from = new DateTime(to).minusDays(40).toDate();
            log.info("Try to run job with params: FROM " + DATE_FORMAT.format(from) + " TO " + DATE_FORMAT.format(to) + " filter " + pluginSettings.filter);
            executor.generateXmlAndStore(DATE_FORMAT.format(from), DATE_FORMAT.format(to), pluginSettings.filter, user);
            return JobRunnerResponse.success();
        } catch (Exception e) {
            log.error("Job runs with error", e);
            return JobRunnerResponse.failed(e);
        }

    }
}
