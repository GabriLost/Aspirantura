package ru.sbertech.atlas.jira.cupintegration.out.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpUtil.class})
public class HttpUtilTest {

    @Test
    public void testSendPostRequest_NotNullParams_CallHttpClientExecute() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        whenNew(HttpClient.class).withNoArguments().thenReturn(httpClient);
        PostMethod postMethod = mock(PostMethod.class);
        whenNew(PostMethod.class).withArguments(eq("http://test")).thenReturn(postMethod);

        Map<String, String> body = new HashMap<>();
        body.put("param1", "value1");

        HttpUtil.sendPostRequest("http://test", body);
        verify(httpClient, times(1)).executeMethod(postMethod);
        verify(postMethod, times(1)).addParameter(eq("param1"), eq("value1"));
    }


}