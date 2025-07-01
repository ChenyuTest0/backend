package com.accenture.acts.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import com.accenture.acts.utils.MaskingConverterUtils;

/**
 * Logbackの特定のクラスの機微情報およびフィールドをマスクするためのMessageConverter。
 *
 * <p>
 * <i>logback-spring.xml</i> などの設定ファイルに以下の値を追加する。
 *
 * <pre>
 *     {@code
 *         <configuration>
 *             <springProperty name="SENSITIVE_ENV_KEYS" scope="system" source=
"masking.envVariables" />
 *             <conversionRule conversionWord="m" converterClass=
"com.accenture.acts.logback.SensitiveValueMessageMaskConverter" />
 *         </configuration>
 *     }
 * </pre>
 *
 * <p>
 * 上記例では、Encoderのパターン{@code %m}にこのConverterを登録する。
 *
 * <p>
 * アプリケーションプロパティ{@code masking.envVariables}で定義された環境変数の値をマスクする。
 *
 * <p>
 * マスクのロジックについては、{@link LogMaskingProcessor#getMask}を参照。
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class SensitiveValueMessageMaskConverter extends MessageConverter {

    private static final ClassicConverter validationResultConverter = new MessageMaskConverter();
    private String[][] maskingArray;

    /**
     * コンストラクタ。
     */
    public SensitiveValueMessageMaskConverter() {
        super();
    }

    /**
     * ログメッセージをマスクするコンバータ。 {@link MessageMaskConverter#convert}も参照。
     *
     * @param event ログイベント。
     * @return マスクされたメッセージ。
     */
    @Override
    public String convert(ILoggingEvent event) {
        if (this.maskingArray == null) {
            this.maskingArray = MaskingConverterUtils.initializingMaskMap(this);
        }
        return MaskingConverterUtils.maskMessage(validationResultConverter.convert(event), this.maskingArray);
    }
}
