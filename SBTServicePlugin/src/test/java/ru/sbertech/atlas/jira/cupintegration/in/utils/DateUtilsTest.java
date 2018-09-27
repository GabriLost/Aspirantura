package ru.sbertech.atlas.jira.cupintegration.in.utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.sbertech.atlas.jira.cupintegration.in.exception.ImportException;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * @author Dmitriev Vladimir
 */
public class DateUtilsTest {
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void testParseDate_incorrectDateFormat_ImportException() throws Exception {
        String inputParam = "20/01/2016";

        thrown.expect(ImportException.class);
        thrown.expectMessage("The date format is not correct");

        DateUtils.parseDate(inputParam);
    }

    @Test
    public void testParseDate_dateFormat_ImportException() throws Exception {
        String inputParam = "20.01.2016";

        Calendar expect = Calendar.getInstance();
        expect.setTimeInMillis(1453237200000L);

        Calendar result = Calendar.getInstance();
        result.setTime(DateUtils.parseDate(inputParam));

        assertEquals(expect.get(Calendar.YEAR), result.get(Calendar.YEAR));
        assertEquals(expect.get(Calendar.MONTH), result.get(Calendar.MONTH));
        assertEquals(expect.get(Calendar.DAY_OF_MONTH), result.get(Calendar.DAY_OF_MONTH));
    }
}
