package com.accenture.acts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.retry.annotation.EnableRetry;

/**
 * アプリケーション実行クラス。
 */
// @EnableRetry は SFTPでspring-retryを利用するために必要
@EnableRetry
@SpringBootApplication(scanBasePackages = {"com.accenture.acts", "com.skeleton"})
public class EntryPoint extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EntryPoint.class);
    }

    /**
     * アプリケーションを実行する。
     *
     * <p>
     * 実行時引数を参照するでSonarqubeの「Using command line arguments is security-sensitive」警告が発生する。
     * </p>
     * この警告自体は消すことはできず、セキュリティ上の懸念があるから適切に対応することが必要な警告である。
     *
     * <p>
     * そのため、以下のコメントではセキュリティ上の懸念をどう扱うかの方針を示す。
     * </p>
     * <ul>
     * <li>実行時引数の使用方法を変更する場合はセキュリティ上の懸念事項を再度整理し、実行時引数の使用が妥当であることを記述すること。</li>
     * </ul>
     *
     * <p>
     * このアプリケーションでは実行時引数を参照してもセキュリティリスクは低いと考え、サニタイズ等の対応はしない。 その理由は次の通り。
     * </p>
     * <ol>
     * <li>SpringBootの設定値を変更するためにのみ実行時引数を使用するため、SQL Injection等の危険性は無い。</li>
     * <li>本アプリケーションの実行時引数はDockerImage内で完結するため、ユーザ入力等の想定していない値を渡すことは困難。</li>
     * </ol>
     *
     * @param args 実行時引数
     */
    public static void main(String[] args) {
        SpringApplication.run(EntryPoint.class, args);
    }
}
