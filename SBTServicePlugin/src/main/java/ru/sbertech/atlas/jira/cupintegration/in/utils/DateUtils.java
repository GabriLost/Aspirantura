package ru.sbertech.atlas.jira.cupintegration.in.utils;

import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Dmitriev Vladimir
 */
public abstract class DateUtils {

    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String ERROR_INCORRECT_DATE = "The date format is not correct";

    public static Date parseDate(String date) throws ImportException {
        try {
            DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            return date.length() == 0 ? null : dateFormat.parse(date);
        } catch (ParseException e) {
            throw new ImportException(ERROR_INCORRECT_DATE);
        }
    }
}
