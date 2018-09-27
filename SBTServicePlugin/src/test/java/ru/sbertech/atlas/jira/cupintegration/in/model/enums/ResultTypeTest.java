package ru.sbertech.atlas.jira.cupintegration.in.model.enums;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Yaroslav Astafiev on 18/03/2016.
 * Department of analytical solutions and system services improvement.
 */
public class ResultTypeTest {

    @Test
    public void testEnumValuesCount() throws Exception {
        assertEquals(3, ResultType.values().length);
    }

    @Test
    public void equalsType() throws Exception {
        assertTrue(ResultType.ISSUE.equalsType("Issue"));
        assertTrue(ResultType.RELEASE.equalsType("Release"));
        assertTrue(ResultType.UNDEFINED.equalsType("Undefined"));

        assertFalse(ResultType.ISSUE.equalsType("Release"));
        assertFalse(ResultType.ISSUE.equalsType("Undefined"));
        assertFalse(ResultType.ISSUE.equalsType(null));
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("Issue", ResultType.ISSUE.toString());
        assertEquals("Release", ResultType.RELEASE.toString());
        assertEquals("Undefined", ResultType.UNDEFINED.toString());
    }

    @Test
    public void forValue_ignoreCase() throws Exception {
        assertEquals(ResultType.ISSUE, ResultType.forValue("issue"));
        assertEquals(ResultType.ISSUE, ResultType.forValue("Issue"));

        assertEquals(ResultType.RELEASE, ResultType.forValue("Release"));
        assertEquals(ResultType.RELEASE, ResultType.forValue("RELEASE"));

        assertEquals(ResultType.UNDEFINED, ResultType.forValue("undefined"));
        assertEquals(ResultType.UNDEFINED, ResultType.forValue("UNDEFINED"));

        assertNull(ResultType.forValue(null));
        assertNull(ResultType.forValue("SomeString"));
    }
}
