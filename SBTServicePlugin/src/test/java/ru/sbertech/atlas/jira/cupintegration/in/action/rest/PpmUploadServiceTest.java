package ru.sbertech.atlas.jira.cupintegration.in.action.rest;

import com.atlassian.plugins.rest.common.multipart.fileupload.CommonsFileUploadFilePart;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.sbertech.atlas.jira.cupintegration.in.service.ObjectsUploadService;

import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Dmitriev Vladimir
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CommonsFileUploadFilePart.class)
public class PpmUploadServiceTest {
    private PpmUploadService ppmUploadService;

    @Before
    public void setup() {
        ObjectsUploadService mockUploadService = mock(ObjectsUploadService.class);
        ppmUploadService = new PpmUploadService(mockUploadService);
    }

    @Test
    public void testUploadFile_Null_NotAcceptable() throws Exception {
        Response result = ppmUploadService.uploadFile(null);

        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), result.getStatus());
        assertEquals("File wasn't uploaded", result.getEntity());
    }

    @Test
    public void testUploadFile_FilePath_OkResponse() throws Exception {
        CommonsFileUploadFilePart filePart = PowerMockito.mock(CommonsFileUploadFilePart.class);
        InputStream stubInputStream = IOUtils.toInputStream("test data");
        when(filePart.getInputStream()).thenReturn(stubInputStream);

        Response result = ppmUploadService.uploadFile(filePart);

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
    }

    @Test
    public void testUploadFile_FilePath_NotAcceptable() throws Exception {
        CommonsFileUploadFilePart filePart = PowerMockito.mock(CommonsFileUploadFilePart.class);
        when(filePart.getInputStream()).thenThrow(new IOException());

        Response result = ppmUploadService.uploadFile(filePart);

        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), result.getStatus());
        assertEquals("File is not provided properly", result.getEntity());
    }
}
