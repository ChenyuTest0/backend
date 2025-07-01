package com.accenture.acts.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;

import com.accenture.acts.utils.MaskingConverterUtils;

/**
 * スタックトレース内の機微情報を所定の形式でマスキングする。マスキングロジックは、{@link LogMaskingProcessor#getMask}を参照。
 *
 * <p>
 * 設定方法は以下の通り。
 *
 * <p>
 * ①logback設定ファイルとアペンダーファイルに以下を設定する。
 *
 * <p>
 * Logback設定ファイル。例：logback-spring.xml
 *
 * <pre>
 *     {@code
 *         <configuration>
 *             <conversionRule conversionWord="ex" converterClass=
"com.accenture.acts.logback.MaskingShortenedThrowableConverter" />
 *         </configuration>
 *     }
 * </pre>
 *
 * <p>
 * アペンダーファイル。例：console.xml
 *
 * <pre>
 *     {@code
 *         <configuration>
 *             <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p %-50c{5} - %m%n%ex</pattern>
 *         </configuration>
 *     }
 * </pre>
 *
 * <p>
 * ②アペンダーファイルに以下を設定する。
 *
 * <pre>
 *     {@code
 *         <stackTrace>
 *             <throwableConverter class=
"net.logstash.logback.stacktrace.ShortenedThrowableConverter">
 *             </throwableConverter>
 *         </stackTrace>
 *     }
 * </pre>
 *
 * <p>
 * マスキングロジックは、{@link LogMaskingProcessor#getMask}を参照。
 *
 * <p>
 * マスキング機能以外の機能は、{@link ShortenedThrowableConverter}と同等。
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class MaskingShortenedThrowableConverter extends ShortenedThrowableConverter {

    private String[][] maskingArray;

    /**
     * コンストラクタ。
     */
    public MaskingShortenedThrowableConverter() {
        super();
    }

    /**
     * ログメッセージをマスクするコンバータ。
     *
     * @param event ログイベント。
     * @return マスクされたメッセージ。
     */
    @Override
    public String convert(ILoggingEvent event) {
        if (this.maskingArray == null) {
            this.maskingArray = MaskingConverterUtils.initializingMaskMap(this);
        }
        return MaskingConverterUtils.maskMessage(super.convert(event), this.maskingArray);
    }

}
