package ru.sbertech.atlas.jira.cupintegration.out.model;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Yaroslav Astafiev on 28/12/2015.
 * Department of analytical solutions and system services improvement.
 */
public class WorkLogContainerTest {

    @Test
    public void testEquals() throws Exception {
        Date date = new Date();
        long timeSpent = 100000000L;
        Long timeSpent2 = 100000000L;
        Long timeSpent3 = 0L;
        WorkLogContainer container = new WorkLogContainer(date, timeSpent);
        WorkLogContainer container1 = new WorkLogContainer(date, timeSpent2);
        WorkLogContainer container2 = new WorkLogContainer(null, timeSpent2);
        WorkLogContainer container3 = new WorkLogContainer(date, timeSpent3);

        assertNotEquals(container, null);
        assertEquals(container, container);
        assertEquals(container, container1);
        assertNotEquals(container, container3);
        assertNotEquals(container1, container2);
    }

    @Test
    public void testGetTimeSpent_Zero() throws Exception {
        Date date = new Date();
        long timeSpent = 0L;
        float timeExpected = 0f;
        WorkLogContainer container = new WorkLogContainer(date, timeSpent);
        //verify actual
        assertEquals(timeExpected, container.getTimeSpent(), 0);
        assertEquals("0,00", container.getTimeSpentFormatted());
    }

    @Test
    public void testGetStartDate() throws Exception {
        Date date = new Date(1100000000);
        long timeSpent = 0L;
        WorkLogContainer container = new WorkLogContainer(date, timeSpent);
        assertEquals(date, container.getStartDate());
        assertEquals("1970-01-13", container.getStartDateFormatted());
    }

    @Test
    public void testTimeSpent_MaxLong() throws Exception {
        Date date = new Date();
        long timeSpent = 8000000;
        float expectedHours = ((float) timeSpent) / 3600;
        WorkLogContainer container = new WorkLogContainer(date, timeSpent);
        assertEquals(expectedHours, container.getTimeSpent(), 0);
        assertEquals("2222,22", container.getTimeSpentFormatted());
    }

    @Test
    public void testHashCode() throws Exception {
        Date date = new Date();
        long timeSpent = 0L;
        WorkLogContainer container = new WorkLogContainer(date, timeSpent);
        WorkLogContainer container1 = new WorkLogContainer(date, timeSpent);

        assertEquals(container.hashCode(), container1.hashCode());
    }
}
