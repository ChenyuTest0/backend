package com.accenture.acts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.accenture.acts.utils.ActsDateUtils;

/**
 * Service設定クラス。
 */
@Configuration
@Component
public class ServiceConfig {
    @Value("${deemed.date.use.flag}")
    private String deemedDateUseFlag;

    /**
     * みなし日初期設定。
     *
     * @return MethodInvokingFactoryBean
     */
    @Bean
    public MethodInvokingFactoryBean initBeanStatic() {
        MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
        bean.setTargetClass(ActsDateUtils.class);
        bean.setTargetMethod("setDeemedDateUseFlag");
        Object[] arguments = {deemedDateUseFlag};
        bean.setArguments(arguments);
        return bean;
    }
}
