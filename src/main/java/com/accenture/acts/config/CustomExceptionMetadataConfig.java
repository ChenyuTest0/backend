package com.accenture.acts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.accenture.acts.registry.ExceptionMetadataRegistry;
import com.accenture.acts.rest.ExceptionMetadata;

/**
 * 例外発生時に応答するメッセージやHttpStatusCodeを設定する{@link ExceptionMetadata}を例外ごとに設定する。
 */
@Configuration
public class CustomExceptionMetadataConfig extends ExceptionMetadataConfig {

    @Bean
    @Override
    public ExceptionMetadataRegistry createExceptionMetadataRegistry() {
        // デフォルトのConfigからRegistryを取得する。
        return super.createExceptionMetadataRegistry();
    }

}
