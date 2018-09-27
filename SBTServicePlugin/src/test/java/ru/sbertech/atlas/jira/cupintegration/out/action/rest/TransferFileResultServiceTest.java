package ru.sbertech.atlas.jira.cupintegration.out.action.rest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;

/**
 * Created by Yaroslav Astafiev on 21/03/2016.
 * Department of analytical solutions and system services improvement.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({LoggerFactory.class})
public class TransferFileResultServiceTest {

    private static Logger mockLOG;

    @BeforeClass
    public static void setup() {
        mockLOG = mock(Logger.class);
        PowerMockito.mockStatic(LoggerFactory.class);
        when(LoggerFactory.getLogger(TransferFileResultService.class)).thenReturn(mockLOG);
    }

    @Test
    public void testSendResult_NullError() throws Exception {
        reset(mockLOG);

        TransferFileResultService transferFileResultService = new TransferFileResultService();

        transferFileResultService.sendResult("some_file", null);

        verify(mockLOG, times(1)).info("File with path: some_file was transferred successful");
        verify(mockLOG, times(0)).error(anyString());
    }

    @Test
    public void testSendResult_EmptyStringError() throws Exception {
        reset(mockLOG);

        TransferFileResultService transferFileResultService = new TransferFileResultService();

        transferFileResultService.sendResult("some_file", "");

        verify(mockLOG, times(1)).info("File with path: some_file was transferred successful");
        verify(mockLOG, times(0)).error(anyString());
    }

    @Test
    public void testSendResult_ContainsError() throws Exception {
        reset(mockLOG);

        TransferFileResultService transferFileResultService = new TransferFileResultService();

        transferFileResultService.sendResult("some_file", "Unexpected Error");

        verify(mockLOG, times(0)).info(anyString());
        verify(mockLOG, times(1)).error("File with path: some_file was transferred with error: Unexpected Error");
    }

    @Test
    public void testSendResult_ContainsError_NullFileName() throws Exception {
        reset(mockLOG);

        TransferFileResultService transferFileResultService = new TransferFileResultService();

        transferFileResultService.sendResult(null, "Unexpected Error");

        verify(mockLOG, times(0)).info(anyString());
        verify(mockLOG, times(1)).error("File with path: null was transferred with error: Unexpected Error");
    }

    @Test
    public void testSendResult_ContainsError_NullFileNameAndError() throws Exception {
        reset(mockLOG);

        TransferFileResultService transferFileResultService = new TransferFileResultService();

        transferFileResultService.sendResult(null, null);

        verify(mockLOG, times(1)).info("File with path: null was transferred successful");
        verify(mockLOG, times(0)).error(anyString());
    }
}
