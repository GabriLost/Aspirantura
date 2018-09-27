package ru.sbertech.atlas.jira.cupintegration.in.service;

import org.apache.commons.lang.NullArgumentException;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.exception.ParseException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Sedelnikov FM on 29/12/2015.
 * Department of analytical solutions and system services improvement.
 */
public class XmlFileStaxReaderTest {

    @Test
    public void testReadXml_OneObjectInXml_ParsedParametersList() {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("oneTask.xml");
        List<Map<String, String>> expect = new ArrayList<>();
        Map<String, String> taskMap = new HashMap<>();
        taskMap.put("ppm_object_type", "Задача КРП");
        taskMap.put("ppm_task_id", "1949921");
        taskMap.put("ppm_task_name", "Подготовка комплекта документов на открытие предпроекта");
        taskMap.put("ppm_task_status", "In Progress");
        taskMap.put("ppm_task_ismilestone", "True");
        taskMap.put("ppm_task_issummary", "False");
        taskMap.put("ppm_task_start_date", "2015-11-13T08:00:00");
        taskMap.put("ppm_task_finish_date", "2015-11-13T08:00:00");
        taskMap.put("ppm_task_IDResourse", "222194");
        taskMap.put("ppm_task_FullnameResource", "Новоселова, Екатерина Александровна");
        taskMap.put("ppm_task_schedulledeffort", "20");
        expect.add(taskMap);

        List<Map<String, String>> result = new ArrayList<>();
        XmlFileStaxReader xmlFileStaxReader = new XmlFileStaxReader(inputStream, "task");
        while (xmlFileStaxReader.hasNext()) {
            result.add(xmlFileStaxReader.next());
        }

        assertEquals(expect, result);
    }

    @Test
    public void testReadXml_TwoObjectInXml_ParsedParametersList() {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("twoTasks.xml");
        List<Map<String, String>> expect = new ArrayList<>();
        Map<String, String> taskMap = new HashMap<>();
        taskMap.put("ppm_object_type", "Задача КРП");
        taskMap.put("ppm_task_id", "1949921");
        taskMap.put("ppm_task_name", "Подготовка комплекта документов на открытие предпроекта");
        taskMap.put("ppm_task_status", "In Progress");
        taskMap.put("ppm_task_ismilestone", "True");
        taskMap.put("ppm_task_issummary", "False");
        taskMap.put("ppm_task_start_date", "2015-11-13T08:00:00");
        taskMap.put("ppm_task_finish_date", "2015-11-13T08:00:00");
        taskMap.put("ppm_task_IDResourse", "222194");
        taskMap.put("ppm_task_FullnameResource", "Новоселова, Екатерина Александровна");
        taskMap.put("ppm_task_schedulledeffort", "20");
        expect.add(taskMap);
        taskMap = new HashMap<>();
        taskMap.put("ppm_object_type", "Задача КРП");
        taskMap.put("ppm_task_id", "1949923");
        taskMap.put("ppm_task_name", "Согласование комплекта документов на открытие предпроекта");
        taskMap.put("ppm_task_status", "Pending Predecessor");
        taskMap.put("ppm_task_ismilestone", "False");
        taskMap.put("ppm_task_issummary", "False");
        taskMap.put("ppm_task_start_date", "2015-11-27T08:00:00");
        taskMap.put("ppm_task_finish_date", "2015-11-27T16:00:00");
        taskMap.put("ppm_task_IDResourse", "220688");
        taskMap.put("ppm_task_FullnameResource", "Грицай, Виталий Владимирович");
        taskMap.put("ppm_task_schedulledeffort", "0");
        expect.add(taskMap);

        List<Map<String, String>> result = new ArrayList<>();
        XmlFileStaxReader xmlFileStaxReader = new XmlFileStaxReader(inputStream, "task");
        while (xmlFileStaxReader.hasNext()) {
            result.add(xmlFileStaxReader.next());
        }

        assertEquals(expect, result);
    }

    @Test
    public void testReadXml_NullInputStream_NullArgumentException() {
        //TODO: rewrite EXPECTED EXCEPTION on THROWN
        try {
            new XmlFileStaxReader(null, "task");
        } catch (Exception e) {
            assertThat(e, new IsInstanceOf(NullArgumentException.class));
            assertEquals("inputStream must not be null.", e.getMessage());
        }
    }

    @Test
    public void testReadXml_NonValidXml_ParseException() {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("oneTaskNonValid.xml");
        XmlFileStaxReader xmlFileStaxReader = new XmlFileStaxReader(inputStream, "task");
        //TODO: rewrite EXPECTED EXCEPTION on THROWN
        try {
            List<Map<String, String>> result = new ArrayList<>();
            while (xmlFileStaxReader.hasNext()) {
                result.add(xmlFileStaxReader.next());
            }
        } catch (Exception e) {
            assertThat(e, new IsInstanceOf(ParseException.class));
            assertEquals("Error while XML parsing", e.getMessage());
        }
    }

}
