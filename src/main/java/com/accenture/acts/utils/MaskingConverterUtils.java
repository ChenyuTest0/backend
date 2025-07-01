package com.accenture.acts.utils;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.accenture.acts.logback.LogMaskingProcessor;

/**
 * ログイベントのマスキングを行うためのユーティリティクラス。
 */
public class MaskingConverterUtils {

    private static int targetIndex = 0;
    private static int maskedIndex = 1;

    /**
     * コンストラクタ。
     */
    private MaskingConverterUtils() {
    }

    /**
     * マスク対象の文字列配列と、マスクされた文字列配列を準備する。
     *
     * @param object 呼び出し元のオブジェクト。
     * @return マスク対象の文字列配列とマスクされた文字列配列の二次元配列。
     */
    public static String[][] initializingMaskMap(Object object) {
        Map<String, String> maskMap = LogMaskingProcessor.preProcessForMaskMap(object);
        String[] targetValues = new String[maskMap.size()];
        String[] maskedValues = new String[maskMap.size()];
        int envVariableCount = 0;

        for (Map.Entry<String, String> entry : maskMap.entrySet()) {
            targetValues[envVariableCount] = entry.getKey();
            maskedValues[envVariableCount++] = entry.getValue();
        }

        return new String[][] {targetValues, maskedValues};
    }

    /**
     * マスキング対象の文字列をマスクしたログメッセージを作成する。
     *
     * @param format ログメッセージ。
     * @param maskingArray マスク対象の文字列配列とマスクされた文字列配列の二次元配列。
     * @return マスキングされたログメッセージ。
     */
    public static String maskMessage(String format, String[][] maskingArray) {
        String[] targetValues = maskingArray[targetIndex];
        String[] maskedValues = maskingArray[maskedIndex];
        if (maskedValues.length == 0) {
            return format;
        }
        return StringUtils.replaceEach(format, targetValues, maskedValues);
    }
}
