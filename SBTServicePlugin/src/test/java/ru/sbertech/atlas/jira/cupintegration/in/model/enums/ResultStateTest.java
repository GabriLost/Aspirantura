package ru.sbertech.atlas.jira.cupintegration.in.model.enums;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Yaroslav Astafiev on 18/03/2016.
 * Department of analytical solutions and system services improvement.
 */
public class ResultStateTest {

    @Test
    public void testEnumValuesCount() throws Exception {
        assertEquals(3, ResultState.values().length);
    }

    @Test
    public void equalsState() throws Exception {
        assertTrue(ResultState.CREATED.equalsState("создан"));
        assertTrue(ResultState.UPDATED.equalsState("обновлен"));
        assertTrue(ResultState.ERROR.equalsState("ошибка"));

        assertFalse(ResultState.CREATED.equalsState("обновлен"));
        assertFalse(ResultState.CREATED.equalsState("ошибка"));
        assertFalse(ResultState.CREATED.equalsState(null));
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("создан", ResultState.CREATED.toString());
        assertEquals("обновлен", ResultState.UPDATED.toString());
        assertEquals("ошибка", ResultState.ERROR.toString());
    }

    @Test
    public void forValue() throws Exception {
        assertEquals(ResultState.CREATED, ResultState.forValue("создан"));
        assertEquals(ResultState.CREATED, ResultState.forValue("СОЗДАН"));

        assertEquals(ResultState.UPDATED, ResultState.forValue("Обновлен"));
        assertEquals(ResultState.UPDATED, ResultState.forValue("ОБНОВЛЕН"));

        assertEquals(ResultState.ERROR, ResultState.forValue("ошибка"));
        assertEquals(ResultState.ERROR, ResultState.forValue("ОШИБКА"));

        assertNull(ResultState.forValue(null));
        assertNull(ResultState.forValue("SomeString"));
    }
}
