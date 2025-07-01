package com.accenture.acts.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.accenture.acts.utils.HttpRequestUtils;

/**
 * リクエストをフィルタしログ出力に必要な情報を保存するためのフィルタ。
 */
public class LoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_URI_KEY = "requestURI";
    private static final String DEVICE_ID_KEY = "deviceId";
    private static final String TRACE_ID_KEY = "traceId";
    private static final String SPAN_ID_KEY = "spanId";
    private static final String REQUEST_ID_KEY = "requestId";

    private static final String DEVICE_ID_HEADER_KEY = "x-device-id";
    // cSpell:disable-next-line
    private static final String TRACE_ID_HEADER_KEY = "x-b3-traceid";
    // cSpell:disable-next-line
    private static final String SPAN_ID_HEADER_KEY = "x-b3-spanid";
    private static final String REQUEST_ID_HEADER_KEY = "x-request-id";

    /**
     * リクエストをフィルタしログ出力に必要な情報を保存する。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        MDC.clear();
        var cachedReq = new ContentCachingRequestWrapper(request);
        MDC.put(REQUEST_URI_KEY, cachedReq.getRequestURI());

        String deviceId = HttpRequestUtils.getHeaderValue(DEVICE_ID_HEADER_KEY);
        if (!StringUtils.isEmpty(deviceId)) {
            MDC.put(DEVICE_ID_KEY, deviceId);
        }

        String traceId = HttpRequestUtils.getHeaderValue(TRACE_ID_HEADER_KEY);
        if (!StringUtils.isEmpty(traceId)) {
            MDC.put(TRACE_ID_KEY, traceId);
        }

        String spanId = HttpRequestUtils.getHeaderValue(SPAN_ID_HEADER_KEY);
        if (!StringUtils.isEmpty(spanId)) {
            MDC.put(SPAN_ID_KEY, spanId);
        }

        String requestId = HttpRequestUtils.getHeaderValue(REQUEST_ID_HEADER_KEY);
        if (!StringUtils.isEmpty(requestId)) {
            MDC.put(REQUEST_ID_KEY, requestId);
        }

        filterChain.doFilter(cachedReq, response);
    }

}
