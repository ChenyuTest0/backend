package com.accenture.acts.utils;

/**
 * 環境変数の値を取得するためのユーティリティクラス。
 */
public class EnvironmentUtils {

    /**
     * コンストラクタ。
     */
    private EnvironmentUtils() {
    }

    /**
     * 環境変数の値を取得するためのラッパーメソッド。
     *
     * @param key 環境変数のキー。
     * @return 環境変数の値。
     */
    public static String getEnvironmentVariable(String key) {
        return System.getenv(key);
    }
}
