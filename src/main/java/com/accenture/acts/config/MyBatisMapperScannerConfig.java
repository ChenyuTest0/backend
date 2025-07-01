package com.accenture.acts.config;

import java.util.Arrays;

import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Repository;

import com.accenture.acts.EntryPoint;
import com.accenture.acts.config.MyBatisMapperScannerConfig.MyBatisMapperScanner;

/**
 * MyBatis用のInterfaceとXMLをScanするためのConfig。
 *
 * <p>
 * EntryPointのSpringBootApplicationアノテーションを参照しており、scanBasePackagesに設定されている値を元にScanを実行する。
 * </p>
 *
 * <p>
 * 対象となるInterfaceは{@link org.springframework.stereotype.Repository}アノテーションが付与されている必要がある。
 * </p>
 */
@Configuration
@Import({MyBatisMapperScanner.class})
@SuppressWarnings("squid:S1118") // ConstructorをPrivateにすべきという旨だが、SpringがInstance化出来なくなるため警告を抑制。
public class MyBatisMapperScannerConfig {

    /**
     * MyBatis用のInterfaceとXMLをScanする。
     */
    public static class MyBatisMapperScanner
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

        private static final Logger LOGGER = LoggerFactory.getLogger(MyBatisMapperScanner.class);

        private ResourceLoader resourceLoader;
        private Environment environment;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry) {

            // EntryPointに付与されている@SpringBootApplicationからscanBasePackagesの値を取得する。
            var springBootApplication = AnnotationUtils.findAnnotation(EntryPoint.class, SpringBootApplication.class);
            var packages = new String[] {};
            if (springBootApplication != null) {
                packages = springBootApplication.scanBasePackages();
            }
            if (LOGGER.isDebugEnabled()) {
                Arrays.asList(packages).forEach(pkg -> LOGGER.debug("Using base package '{}'", pkg));
            }

            // Scan実行
            var scanner = new ClassPathMapperScanner(registry, environment);
            if (this.resourceLoader != null) {
                scanner.setResourceLoader(this.resourceLoader);
            }
            scanner.setAnnotationClass(Repository.class);
            scanner.setSqlSessionFactoryBeanName("sqlSessionFactory");
            scanner.registerFilters();
            scanner.doScan(packages);
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }
    }
}
