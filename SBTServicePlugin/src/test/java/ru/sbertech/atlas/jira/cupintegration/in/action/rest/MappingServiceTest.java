package ru.sbertech.atlas.jira.cupintegration.in.action.rest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.api.mockito.PowerMockito;
import ru.sbertech.atlas.jira.cupintegration.in.model.JsonMapping;
import ru.sbertech.atlas.jira.cupintegration.in.model.MappingEntity;
import ru.sbertech.atlas.jira.cupintegration.in.repository.IMappingRepository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dmitriev Vladimir
 */
public class MappingServiceTest {
    private IMappingRepository mockMappingRepository;
    private MappingService mappingService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        mockMappingRepository = PowerMockito.mock(IMappingRepository.class);
        mappingService = new MappingService(mockMappingRepository);
    }

    @Test
    public void testGetAll_MappingList_OkResponse() throws Exception {
        final MappingEntity firstMappingEntity = mock(MappingEntity.class);
        when(firstMappingEntity.getFieldId()).thenReturn("firstFieldId");
        when(firstMappingEntity.getFieldName()).thenReturn("firstFieldName");
        when(firstMappingEntity.getFieldType()).thenReturn("firstFieldType");
        when(firstMappingEntity.getXmlId()).thenReturn("firstXmlId");

        MappingEntity[] mappingEntities = new MappingEntity[1];
        mappingEntities[0] = firstMappingEntity;

        when(mockMappingRepository.getAll()).thenReturn(mappingEntities);

        JsonMapping[] result = mappingService.getAll();

        assertEquals(firstMappingEntity.getFieldId(), result[0].getFieldId());
        assertEquals(firstMappingEntity.getFieldName(), result[0].getFieldName());
        assertEquals(firstMappingEntity.getFieldType(), result[0].getFieldType());
        assertEquals(firstMappingEntity.getXmlId(), result[0].getXmlId());
    }

    @Test
    public void testGetAll_EmptyList_OkResponse() throws Exception {
        MappingEntity[] mappingEntities = new MappingEntity[0];

        when(mockMappingRepository.getAll()).thenReturn(mappingEntities);
        MappingService mappingService = new MappingService(mockMappingRepository);

        JsonMapping[] result = mappingService.getAll();

        assertTrue(result.length == 0);
    }

    @Test
    public void testUpdate_MappingList_OkResponse() throws Exception {
        final JsonMapping firstJsonMapping = mock(JsonMapping.class);
        when(firstJsonMapping.getFieldId()).thenReturn("firstFieldId");
        when(firstJsonMapping.getFieldName()).thenReturn("firstFieldName");
        when(firstJsonMapping.getFieldType()).thenReturn("firstFieldType");
        when(firstJsonMapping.getXmlId()).thenReturn("firstXmlId");

        JsonMapping[] jsonMappings = new JsonMapping[1];
        jsonMappings[0] = firstJsonMapping;

        final MappingEntity returnMappingEntity = mock(MappingEntity.class);
        when(returnMappingEntity.getFieldId()).thenReturn("firstFieldId");
        when(returnMappingEntity.getFieldName()).thenReturn("firstFieldName");
        when(returnMappingEntity.getFieldType()).thenReturn("firstFieldType");
        when(returnMappingEntity.getXmlId()).thenReturn("firstXmlId");

        MappingEntity[] mappingEntities = new MappingEntity[1];
        mappingEntities[0] = returnMappingEntity;

        when(mockMappingRepository.deleteAll()).thenReturn(new MappingEntity[0]);
        when(mockMappingRepository.createAll(jsonMappings)).thenReturn(mappingEntities);

        JsonMapping[] result = mappingService.update(jsonMappings);

        assertEquals(firstJsonMapping.getFieldId(), result[0].getFieldId());
        assertEquals(firstJsonMapping.getFieldName(), result[0].getFieldName());
        assertEquals(firstJsonMapping.getFieldType(), result[0].getFieldType());
        assertEquals(firstJsonMapping.getXmlId(), result[0].getXmlId());
    }

    @Test
    public void testUpdate_EmptyList_NegativeArraySizeException() throws Exception {
        JsonMapping[] jsonMappings = new JsonMapping[0];

        when(mockMappingRepository.deleteAll()).thenReturn(new MappingEntity[0]);
        when(mockMappingRepository.createAll(jsonMappings)).thenThrow(new NegativeArraySizeException());

        thrown.expect(NegativeArraySizeException.class);

        mappingService.update(jsonMappings);
    }
}
