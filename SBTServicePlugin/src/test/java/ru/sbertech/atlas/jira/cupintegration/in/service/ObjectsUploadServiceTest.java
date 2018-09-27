package ru.sbertech.atlas.jira.cupintegration.in.service;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportSettings;
import ru.sbertech.atlas.jira.cupintegration.in.model.ReleaseMapping;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultState;
import ru.sbertech.atlas.jira.cupintegration.in.model.enums.ResultType;
import ru.sbertech.atlas.jira.cupintegration.in.validator.ImportSettingsChecker;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by Sedelnikov FM on 26/01/2016.
 * Department of analytical solutions and system services improvement.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ObjectsUploadService.class})
public class ObjectsUploadServiceTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testUpload_nullInputStream_nullArgumentException() throws Exception {

        ImportSettingService importSettingService = mock(ImportSettingService.class);
        ImportSettings importSettings = mock(ImportSettings.class);
        when(importSettingService.getImportSetting()).thenReturn(importSettings);

        ReleaseMappingService releaseMappingService = mock(ReleaseMappingService.class);
        ReleaseMapping releaseMapping = mock(ReleaseMapping.class);
        when(releaseMappingService.getReleaseMapping()).thenReturn(releaseMapping);

        ObjectsUploadService objectsUploadService = new ObjectsUploadService(new IssueImportService(null, null, null, null, null),
            new ReleaseImportService(null, null, null, null, null, null),
            importSettingService, releaseMappingService);

        ImportSettingsChecker importSettingsChecker = mock(ImportSettingsChecker.class);
        when(importSettingsChecker.checkCustomField(anyString())).thenReturn(importSettingsChecker);
        when(importSettingsChecker.checkUserName(anyString())).thenReturn(importSettingsChecker);
        PowerMockito.whenNew(ImportSettingsChecker.class).withAnyArguments().thenReturn(importSettingsChecker);

        exception.expect(NullArgumentException.class);
        exception.expectMessage("inputStream must not be null.");

        objectsUploadService.upload(null);
    }

    @Test
    public void testUpload_validInputStream_ErrorInResult() throws Exception {

        XmlFileStaxReader xmlFileStaxReader = mock(XmlFileStaxReader.class);
        when(xmlFileStaxReader.hasNext()).thenReturn(true).thenReturn(false);
        @SuppressWarnings("unchecked")
        Map<String, String> params = mock(Map.class);
        when(params.get("ppm_object_type")).thenThrow(new RuntimeException("test exception"));
        when(xmlFileStaxReader.next()).thenReturn(params);
        IssueImportService issueImportService = mock(IssueImportService.class);
        InputStream inputStream = mock(InputStream.class);
        ImportSettingService importSettingService = mock(ImportSettingService.class);
        ReleaseImportService releaseImportService = mock(ReleaseImportService.class);
        ImportSettings importSettings = mock(ImportSettings.class);
        when(importSettingService.getImportSetting()).thenReturn(importSettings);
        ReleaseMappingService releaseMappingService = mock(ReleaseMappingService.class);
        ReleaseMapping releaseMapping = mock(ReleaseMapping.class);
        when(releaseMappingService.getReleaseMapping()).thenReturn(releaseMapping);
        // при переменном числе параметров надо явно задавать ожидаемое(используемое в тестируемом классе) количество аргументов
        PowerMockito.whenNew(XmlFileStaxReader.class).withArguments(any(), any(), any()).thenReturn(xmlFileStaxReader);
        ImportSettingsChecker importSettingsChecker = mock(ImportSettingsChecker.class);
        when(importSettingsChecker.checkCustomField(anyString())).thenReturn(importSettingsChecker);
        when(importSettingsChecker.checkUserName(anyString())).thenReturn(importSettingsChecker);
        PowerMockito.whenNew(ImportSettingsChecker.class).withAnyArguments().thenReturn(importSettingsChecker);
        ObjectsUploadService objectsUploadService = new ObjectsUploadService(issueImportService, releaseImportService, importSettingService, releaseMappingService);
        List<ImportResult> resultList = objectsUploadService.upload(inputStream);

        assertEquals(1, resultList.size());
        ImportResult resultObject = resultList.get(0);
        assertEquals(resultObject.getState(), ResultState.ERROR);
        assertEquals(resultObject.getType(), ResultType.UNDEFINED);
        assertEquals("test exception", resultObject.getValue());
    }

    @Test
    public void testUpload_inputStreamWithNullObjectType_ErrorInResult() throws Exception {

        XmlFileStaxReader xmlFileStaxReader = mock(XmlFileStaxReader.class);
        when(xmlFileStaxReader.hasNext()).thenReturn(true).thenReturn(false);
        @SuppressWarnings("unchecked")
        Map<String,String> params = mock(Map.class);
        when(params.get("ppm_object_type")).thenReturn(null);
        when(xmlFileStaxReader.next()).thenReturn(params);
        IssueImportService issueImportService = mock(IssueImportService.class);
        ImportSettingService importSettingService = mock(ImportSettingService.class);
        ReleaseImportService releaseImportService = mock(ReleaseImportService.class);
        ImportSettings importSettings = mock(ImportSettings.class);
        when(importSettingService.getImportSetting()).thenReturn(importSettings);
        ReleaseMappingService releaseMappingService = mock(ReleaseMappingService.class);
        ReleaseMapping releaseMapping = mock(ReleaseMapping.class);
        when(releaseMappingService.getReleaseMapping()).thenReturn(releaseMapping);
        InputStream inputStream = mock(InputStream.class);
        // при переменном числе параметров надо явно задавать ожидаемое(используемое в тестируемом классе) количество аргументов
        PowerMockito.whenNew(XmlFileStaxReader.class).withArguments(any(), any(), any()).thenReturn(xmlFileStaxReader);
        ImportSettingsChecker importSettingsChecker = mock(ImportSettingsChecker.class);
        when(importSettingsChecker.checkCustomField(anyString())).thenReturn(importSettingsChecker);
        when(importSettingsChecker.checkUserName(anyString())).thenReturn(importSettingsChecker);
        PowerMockito.whenNew(ImportSettingsChecker.class).withAnyArguments().thenReturn(importSettingsChecker);
        ObjectsUploadService objectsUploadService = new ObjectsUploadService(issueImportService, releaseImportService, importSettingService,releaseMappingService);
        List<ImportResult> resultList = objectsUploadService.upload(inputStream);

        assertEquals(1, resultList.size());
        ImportResult resultObject = resultList.get(0);
        assertEquals(resultObject.getState(), ResultState.ERROR);
        assertEquals(resultObject.getType(), ResultType.UNDEFINED);
        assertEquals("Undefined object! Object type tag is absent", resultObject.getValue());

    }

    @Test
    public void testUpload_inputStreamWithXml_CreatedIssue() throws Exception {


        XmlFileStaxReader xmlFileStaxReader = mock(XmlFileStaxReader.class);
        when(xmlFileStaxReader.hasNext()).thenReturn(true).thenReturn(false);
        @SuppressWarnings("unchecked")
        Map<String,String> params = mock(Map.class);
        when(params.get("ppm_object_type")).thenReturn("ЗНИ");
        when(xmlFileStaxReader.next()).thenReturn(params);
        IssueImportService issueImportService = mock(IssueImportService.class);
        ImportResult importResult = new ImportResult(ResultType.ISSUE, ResultState.CREATED, "test-ZNI-1");
        when(issueImportService.importObject(params)).thenReturn(importResult);
        InputStream inputStream = mock(InputStream.class);
        ImportSettingService importSettingService = mock(ImportSettingService.class);
        ReleaseImportService releaseImportService = mock(ReleaseImportService.class);
        ImportSettings importSettings = mock(ImportSettings.class);
        when(importSettingService.getImportSetting()).thenReturn(importSettings);
        ReleaseMappingService releaseMappingService = mock(ReleaseMappingService.class);
        ReleaseMapping releaseMapping = mock(ReleaseMapping.class);
        when(releaseMappingService.getReleaseMapping()).thenReturn(releaseMapping);
        // при переменном числе параметров надо явно задавать ожидаемое(используемое в тестируемом классе) количество аргументов
        PowerMockito.whenNew(XmlFileStaxReader.class).withArguments(any(), any(), any()).thenReturn(xmlFileStaxReader);
        ObjectsUploadService objectsUploadService = new ObjectsUploadService(issueImportService, releaseImportService, importSettingService, releaseMappingService);
        ImportSettingsChecker importSettingsChecker = mock(ImportSettingsChecker.class);
        when(importSettingsChecker.checkCustomField(anyString())).thenReturn(importSettingsChecker);
        when(importSettingsChecker.checkUserName(anyString())).thenReturn(importSettingsChecker);
        PowerMockito.whenNew(ImportSettingsChecker.class).withAnyArguments().thenReturn(importSettingsChecker);
        List<ImportResult> resultList = objectsUploadService.upload(inputStream);

        assertEquals(1, resultList.size());
        ImportResult result = resultList.get(0);
        assertEquals(ResultState.CREATED, result.getState());
        assertEquals(ResultType.ISSUE, result.getType());
        assertEquals("test-ZNI-1", result.getValue());
    }

    @Test
    public void testUpload_inputStreamWithUnknownObjectType_ErrorInResult() throws Exception {

        XmlFileStaxReader xmlFileStaxReader = mock(XmlFileStaxReader.class);
        when(xmlFileStaxReader.hasNext()).thenReturn(true).thenReturn(false);
        @SuppressWarnings("unchecked")
        Map<String,String> params = mock(Map.class);
        when(params.get("ppm_object_type")).thenReturn("unknownObjectType");
        when(xmlFileStaxReader.next()).thenReturn(params);
        IssueImportService issueImportService = mock(IssueImportService.class);
        InputStream inputStream = mock(InputStream.class);
        ImportSettingService importSettingService = mock(ImportSettingService.class);
        ReleaseImportService releaseImportService = mock(ReleaseImportService.class);
        ImportSettings importSettings = mock(ImportSettings.class);
        when(importSettingService.getImportSetting()).thenReturn(importSettings);
        ReleaseMappingService releaseMappingService = mock(ReleaseMappingService.class);
        ReleaseMapping releaseMapping = mock(ReleaseMapping.class);
        when(releaseMappingService.getReleaseMapping()).thenReturn(releaseMapping);
        // при переменном числе параметров надо явно задавать ожидаемое(используемое в тестируемом классе) количество аргументов
        PowerMockito.whenNew(XmlFileStaxReader.class).withArguments(any(), any(), any()).thenReturn(xmlFileStaxReader);
        ImportSettingsChecker importSettingsChecker = mock(ImportSettingsChecker.class);
        when(importSettingsChecker.checkCustomField(anyString())).thenReturn(importSettingsChecker);
        when(importSettingsChecker.checkUserName(anyString())).thenReturn(importSettingsChecker);
        PowerMockito.whenNew(ImportSettingsChecker.class).withAnyArguments().thenReturn(importSettingsChecker);
        ObjectsUploadService objectsUploadService = new ObjectsUploadService(issueImportService, releaseImportService, importSettingService, releaseMappingService);
        List<ImportResult> resultList = objectsUploadService.upload(inputStream);

        assertEquals(1, resultList.size());
        ImportResult resultObject = resultList.get(0);
        assertEquals(ResultState.ERROR, resultObject.getState());
        assertEquals(ResultType.UNDEFINED, resultObject.getType());
        assertEquals("Undefined object! Unknown object type tag unknownObjectType", resultObject.getValue());
    }

}
