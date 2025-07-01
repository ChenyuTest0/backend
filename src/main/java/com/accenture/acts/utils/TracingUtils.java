package com.accenture.acts.utils;

/**
 * ログのトレースに用いる機能に関するUtils。
 */
public class TracingUtils {
    // cSpell:disable-next-line
    private static final String TRACE_ID_HEADER_KEY = "x-b3-traceid";

    private TracingUtils() {
    }

    /**
     * 現在のリクエストに紐づいているTraceIDを取得する。
     *
     * @return TraceID（取得できなかった場合はNull）
     */
    public static String getTraceId() {
        return HttpRequestUtils.getHeaderValue(TRACE_ID_HEADER_KEY);
    }

}
