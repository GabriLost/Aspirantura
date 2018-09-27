package ru.sbertech.atlas.jira.cupintegration.out.utils;

import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.query.Query;
import org.apache.commons.lang3.StringUtils;

/**
 * This class contains various methods for Timesheets manipulation and input parameters generation
 */
public class TimesheetGeneratorUtils {
    /**
     *
     * Builds jqlQuery by given time range and String representation of jql filter.
     * jqlQuery selects all issues that matches jql filter and have worklog changes at given time range
     *
     * @param fromDate String representation of start date of worklog changes tracking
     * @param toDate String representation of end date of worklog changes tracking
     * @param filter String representation of jql Query for rough issues filtering
     * @return jql Query for given filter and time range
     * @throws JqlParseException when filter or date params are incorrect
     */
    public static Query buildQueryFromFilterAndDates(String fromDate, String toDate, String filter) throws JqlParseException {
        JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        queryBuilder.where().issue().inFunc("worklogPeriod", StringUtils.defaultString(filter), fromDate, toDate);
        return queryBuilder.buildQuery();
    }
}
