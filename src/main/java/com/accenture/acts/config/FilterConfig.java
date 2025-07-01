package com.accenture.acts.config;

import jakarta.servlet.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.filters.HttpHeaderSecurityFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.accenture.acts.filter.ExceptionHandlerFilter;
import com.accenture.acts.filter.LoggingFilter;
import com.accenture.acts.registry.ExceptionMetadataRegistry;

/**
 * フィルター設定クラス。
 */
@Configuration
public class FilterConfig implements WebMvcConfigurer {

    /**
     * HttpHeaderSecurityFilter。
     *
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<HttpHeaderSecurityFilter> httpHeaderSecurityFilter() {
        FilterRegistrationBean<HttpHeaderSecurityFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new HttpHeaderSecurityFilter());
        bean.setOrder(1);
        return bean;
    }

    /**
     * HiddenHttpMethodFilter。
     *
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilter() {
        FilterRegistrationBean<HiddenHttpMethodFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new HiddenHttpMethodFilter());
        bean.setOrder(2);
        return bean;
    }

    @Bean
    public ExceptionHandlerFilter exceptionFilter(ExceptionMetadataRegistry registry, ObjectMapper objectMapper) {
        return new ExceptionHandlerFilter(registry, objectMapper);
    }

    /**
     * ExceptionHandlerFilter。
     *
     * @param exceptionFilter ExceptionHandlerFilter
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<Filter> exceptionHandlerFilter(Filter exceptionFilter) {
        FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
        bean.setFilter(exceptionFilter);
        bean.setOrder(3);
        return bean;
    }

    /**
     * LoggingFilter。
     *
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoggingFilter());
        bean.setOrder(4);
        return bean;
    }
}
