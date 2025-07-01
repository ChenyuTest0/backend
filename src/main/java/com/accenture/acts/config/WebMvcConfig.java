package com.accenture.acts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc設定クラス。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${cors.access-control-allow-origin.allow-all:false}")
    private boolean allowAllOrigin;

    /**
     * バリデーション設定。
     *
     * @param messageSource MessageSource
     * @return LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource);
        return localValidatorFactoryBean;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (allowAllOrigin) {
            registry.addMapping("/**");
        }
    }
}
