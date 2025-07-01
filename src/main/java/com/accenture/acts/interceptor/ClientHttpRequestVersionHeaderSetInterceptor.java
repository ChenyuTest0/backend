package com.accenture.acts.interceptor;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import com.accenture.acts.utils.LogUtil;

/**
 * 外部サービス連携時に外部サービスのリクエストバージョンをヘッダに設定する。
 */
public class ClientHttpRequestVersionHeaderSetInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientHttpRequestVersionHeaderSetInterceptor.class);

    private String requestVersion;

    /**
     * 設定するヘッダバージョンを指定してインスタンスを作成する。
     *
     * @param requestVersion 外部サービスのリクエストバージョン
     */
    public ClientHttpRequestVersionHeaderSetInterceptor(String requestVersion) {
        this.requestVersion = requestVersion;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
        final ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().add("x-request-version", requestVersion);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Http request Header : {}", LogUtil.sanitizeRequestHeader(request.getHeaders()));
        }

        return execution.execute(request, body);
    }
}
