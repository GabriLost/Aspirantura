package ru.sbertech.atlas.jira.cupintegration.in.utils;

import org.apache.commons.lang.NullArgumentException;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;
import ru.sbertech.atlas.jira.cupintegration.in.model.IMapping;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Sedelnikov FM on 19/01/2016.
 * Department of analytical solutions and system services improvement.
 */
public class JiraFieldsParseUtilsTest {

    @Test
    public void testSplitParamMap_NullParamsMap_NullArgumentException() throws Exception {
        try {
            JiraFieldsParseUtils.splitParamMap(null, new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, IMapping>());
        } catch (Exception e) {
            assertThat(e, new IsInstanceOf(NullArgumentException.class));
            assertEquals("params must not be null.", e.getMessage());
        }
    }

    @Test
    public void testSplitParamMap_NullDefaultFieldsMap_NullArgumentException() throws Exception {
        try {
            JiraFieldsParseUtils.splitParamMap(new HashMap<String, String>(), null, new HashMap<String, String>(), new HashMap<String, IMapping>());
        } catch (Exception e) {
            assertThat(e, new IsInstanceOf(NullArgumentException.class));
            assertEquals("defaultFields must not be null.", e.getMessage());
        }
    }

    @Test
    public void testSplitParamMap_NullCustomFieldsMap_NullArgumentException() throws Exception {
        try {
            JiraFieldsParseUtils.splitParamMap(new HashMap<String, String>(), new HashMap<String, String>(), null, new HashMap<String, IMapping>());
        } catch (Exception e) {
            assertThat(e, new IsInstanceOf(NullArgumentException.class));
            assertEquals("customFields must not be null.", e.getMessage());
        }
    }

    @Test
    public void testSplitParamMap_NullMappingMap_NullArgumentException() throws Exception {
        try {
            JiraFieldsParseUtils.splitParamMap(new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>(), null);
        } catch (Exception e) {
            assertThat(e, new IsInstanceOf(NullArgumentException.class));
            assertEquals("mappingMap must not be null.", e.getMessage());
        }
    }

    @Test
    public void testSplitParamMap_MapWithDefaultParams_NullCustomFieldsNotEmptyDefaultFields() {
        Map<String, String> params = new HashMap<>();
        params.put("project", "test_project");
        params.put("summary", "test_issue");
        Map<String, String> defaultFields = new HashMap<>();
        Map<String, String> customFields = new HashMap<>();

        Map<String, String> expectedDefaultFields = new HashMap<>();
        expectedDefaultFields.put("project", "test_project");
        expectedDefaultFields.put("summary", "test_issue");

        IMapping mapping1 = mock(IMapping.class);
        when(mapping1.getFieldId()).thenReturn("project");
        when(mapping1.getFieldType()).thenReturn("Default Field");
        IMapping mapping2 = mock(IMapping.class);
        when(mapping2.getFieldId()).thenReturn("summary");
        when(mapping2.getFieldType()).thenReturn("Default Field");
        Map<String, IMapping> mappingMap = new HashMap<>();
        mappingMap.put("project", mapping1);
        mappingMap.put("summary", mapping2);

        JiraFieldsParseUtils.splitParamMap(params, defaultFields, customFields, mappingMap);

        assertEquals(expectedDefaultFields, defaultFields);
        assertEquals(new HashMap<String, Object>(), customFields);
    }

    @Test
    public void testSplitParamMap_MapWithDefaultAndCustomParams_NotEmptyCustomFieldsNotEmptyDefaultFields() {
        Map<String, String> params = new HashMap<>();
        params.put("project", "test_project");
        params.put("summary", "test_issue");
        params.put("Epic Name", "EPIC");
        Map<String, String> defaultFields = new HashMap<>();
        Map<String, String> customFields = new HashMap<>();

        Map<String, String> expectedDefaultFields = new HashMap<>();
        expectedDefaultFields.put("project", "test_project");
        expectedDefaultFields.put("summary", "test_issue");

        Map<String, String> expectedCustomFields = new HashMap<>();
        expectedCustomFields.put("Epic Name", "EPIC");

        IMapping mapping1 = mock(IMapping.class);
        when(mapping1.getFieldId()).thenReturn("project");
        when(mapping1.getFieldType()).thenReturn("Default Field");
        IMapping mapping2 = mock(IMapping.class);
        when(mapping2.getFieldId()).thenReturn("summary");
        when(mapping2.getFieldType()).thenReturn("Default Field");
        IMapping mapping3 = mock(IMapping.class);
        when(mapping3.getFieldId()).thenReturn("Epic Name");
        when(mapping3.getFieldType()).thenReturn("Custom Field");
        Map<String, IMapping> mappingMap = new HashMap<>();
        mappingMap.put("project", mapping1);
        mappingMap.put("summary", mapping2);
        mappingMap.put("Epic Name", mapping3);

        JiraFieldsParseUtils.splitParamMap(params, defaultFields, customFields, mappingMap);

        assertEquals(expectedDefaultFields, defaultFields);
        assertEquals(expectedCustomFields, customFields);
    }

}
