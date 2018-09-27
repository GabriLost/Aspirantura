package ru.sbertech.atlas.jira.cupintegration.out.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;

public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    public static void sendPostRequest(@NotNull String url, @NotNull Map<String, String> params) {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            postMethod.addParameter(entry.getKey(), entry.getValue());
        }
        try {
            httpClient.executeMethod(postMethod);
        } catch (IOException e) {
            log.error("Can't send http request: ", e);
        }

    }
}
