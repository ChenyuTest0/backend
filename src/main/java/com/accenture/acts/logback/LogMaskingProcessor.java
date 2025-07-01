package com.accenture.acts.logback;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.qos.logback.core.BasicStatusManager;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.util.StatusPrinter2;
import org.apache.commons.lang3.StringUtils;

import com.accenture.acts.utils.EnvironmentUtils;

/**
 * ログマスキングの機能を提供するクラス。
 */
public class LogMaskingProcessor {

    /**
     * マスクに利用する値。
     */
    public static final char MASK_CHARACTER = '*';

    /**
     * マスク対象の環境変数を特定するためのプレースホルダ。
     */
    public static final String SENSITIVE_ENV_HOLDER_PROPERTY = "SENSITIVE_ENV_KEYS";
    private static final String IGNORE_EMPTY_ENV_PROPERTY = "Ignore empty valued property";
    private static final BasicStatusManager basicStatusManager = new BasicStatusManager();
    private static final StatusPrinter2 statusPrinter = new StatusPrinter2();
    private static Map<String, String> cachedMaskMap = null;

    /**
     * プライベートコンストラクタ。
     */
    private LogMaskingProcessor() {
    }

    /**
     * Logbackの設定に関するINFOメッセージを出力する。
     *
     * @param msg ログ出力されるメッセージ。
     */
    private static void addInfo(String msg) {
        addInfo(msg, LogMaskingProcessor.class);
    }

    /**
     * Logbackの設定に関するINFOメッセージを出力する。
     *
     * @param msg ログ出力されるメッセージ。
     * @param declaredOrigin Originクラスのオブジェクト。
     */
    private static void addInfo(String msg, Object declaredOrigin) {
        basicStatusManager.add(new InfoStatus(msg, declaredOrigin));
    }

    /**
     * {@link LogMaskingProcessor#addInfo(String, Object)}によってステータスマネージャーに入れられたステータスを出力する。
     */
    private static void flushStatus() {
        statusPrinter.print(basicStatusManager);
        basicStatusManager.clear();
    }

    /**
     * カンマ区切りで記載された環境変数の文字列リストから、環境変数のセットを作成する。
     *
     * @param envKeyList カンマ区切りの環境変数一覧。
     * @return 環境変数のセット。
     */
    public static Set<String> fetchEnvironmentKeySet(String envKeyList) {
        addInfo("List of Sensitive ENV Properties is [" + envKeyList + "]");
        if (StringUtils.isBlank(envKeyList)) {
            return Set.of();
        }
        return Stream.of(envKeyList.split(",")).map(String::strip).filter(StringUtils::isNotBlank)
            .collect(Collectors.toSet());
    }

    /**
     * マスキングの形式を取得する。
     *
     * <p>
     * マスキングロジックは次の通り：
     * <ul>
     * <li>文字列の長さが31未満の場合
     * <ul>
     * <li>文字列全体をマスクする。例：{@literal "abcde"} の場合、マスクは {@literal "*****"}</li>
     * </ul>
     * </li>
     * <li>それ以外の場合
     * <ul>
     * <li>最初の3文字を保持し、他を {@literal "*"} で置き換える。例：{@literal "e3x6b806-493b-4487-b2a6-8d5a3f9f5d63"}
     * の場合、 マスクは {@literal "e3x*****************************"}</li>
     * </ul>
     * </li>
     * </ul>
     *
     * @param target 入力文字列。
     * @return マスクされた入力文字列。
     */
    public static String getMask(String target) {
        if (StringUtils.isBlank(target)) {
            return StringUtils.EMPTY;
        }
        int l = target.length();
        if (l < 32) {
            return StringUtils.repeat(MASK_CHARACTER, l);
        } else {
            return target.substring(0, 3) + StringUtils.repeat(MASK_CHARACTER, l - 3);
        }
    }

    /**
     * ターゲット値のマスクされた文字列を含むMapを準備する。<i>target</i>は環境変数の値。
     *
     * @param declaredOrigin 呼び出し元のオブジェクト。
     * @return 環境変数の値 -> マスクされた環境変数の値を含むMap。
     */
    public static Map<String, String> preProcessForMaskMap(Object declaredOrigin) {
        if (cachedMaskMap == null) {
            addInfo(" Computing MaskMap for systemPropertyHolder:[" + SENSITIVE_ENV_HOLDER_PROPERTY + "]",
                declaredOrigin);
            cachedMaskMap = processSystemPropertyHolder();
        } else {
            addInfo(" Using cached MaskMap for systemPropertyHolder:[" + SENSITIVE_ENV_HOLDER_PROPERTY + "]",
                declaredOrigin);
        }
        flushStatus();
        return Map.copyOf(cachedMaskMap);
    }

    /**
     * ターゲット値のマスクされた文字列を含むMapを準備するためのヘルパー関数。<i>target</i>の値は環境変数の値である。
     *
     * @return 環境変数の値 -> マスクされた環境変数の値を含むMap。
     */
    private static Map<String, String> processSystemPropertyHolder() {
        String sensitiveEnvVariableString = System.getProperty(SENSITIVE_ENV_HOLDER_PROPERTY, "");
        Set<String> sensitiveEnvVars = LogMaskingProcessor.fetchEnvironmentKeySet(sensitiveEnvVariableString);
        if (sensitiveEnvVars.isEmpty()) {
            addInfo("List of Sensitive ENV Properties value:[" + sensitiveEnvVariableString
                + "] is [EMPTY]. Sensitive Value Masking will be [SKIPPED]");
            return Map.of();
        }
        addInfo("Masking value for environment variables:" + sensitiveEnvVars);
        // Fetch value for each environment variable
        Map<String, String> envVarValueMap = new HashMap<>();
        sensitiveEnvVars.forEach(prop -> envVarValueMap.put(prop, EnvironmentUtils.getEnvironmentVariable(prop)));
        // Warning about empty env variable
        envVarValueMap.entrySet().stream().filter(entry -> StringUtils.isBlank(entry.getValue()))
            .forEach(entry -> addInfo(IGNORE_EMPTY_ENV_PROPERTY + " [" + entry.getKey() + "] in environment "));
        // Prepare MaskMap
        Map<String, String> valueMaskMap = new HashMap<>();
        envVarValueMap.entrySet().stream().filter((entry -> StringUtils.isNotBlank(entry.getValue())))
            .forEach(entry -> valueMaskMap.put(entry.getValue(), getMask(entry.getValue())));
        return valueMaskMap;
    }

}
