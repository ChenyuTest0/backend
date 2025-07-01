package com.accenture.acts.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.accenture.acts.exception.NoRollbackBusinessFailureException;

/**
 * トランザクション設定クラス。
 */
@Aspect
@Configuration
public class TransactionConfig implements WebMvcConfigurer {

    @Bean(initMethod = "getConnection")
    // cSpell:disable-next-line
    @ConfigurationProperties(prefix = "spring.datasource.dbcp2")
    // cSpell:disable-next-line
    @ConditionalOnProperty(name = "spring.datasource.dbcp2")
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.read-write")
    @ConditionalOnProperty(name = "datasource.read-write.url", matchIfMissing = false)
    public DataSourceProperties readWriteDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(initMethod = "getConnection")
    @ConditionalOnBean(name = "readWriteDataSourceProperties")
    public SimpleDriverDataSource readWriteDataSource(
        @Qualifier("readWriteDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(SimpleDriverDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.read-only")
    @ConditionalOnProperty(name = "datasource.read-only.url", matchIfMissing = false)
    public DataSourceProperties readOnlyDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(initMethod = "getConnection")
    @ConditionalOnBean(name = "readOnlyDataSourceProperties")
    public SimpleDriverDataSource readOnlyDataSource(
        @Qualifier("readOnlyDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(SimpleDriverDataSource.class).build();
    }

    /**
     * 指定されたDataSourceから接続をバインドするトランザクションマネージャー。
     *
     * @param routingDataSource dataSource
     * @return DataSourceTransactionManager
     */
    @Bean
    @ConditionalOnBean(name = "routingDataSource")
    public DataSourceTransactionManager dataSourceTransactionManager(
        @Qualifier("routingDataSource") DataSource routingDataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(routingDataSource);
        dataSourceTransactionManager.setRollbackOnCommitFailure(true);
        return dataSourceTransactionManager;
    }

    /**
     * 接続を遅延させて取得することで、DBにリクエストを投げるタイミングで、どちらのDataSourceから接続を取得するのか判定するための設定。
     *
     * @param transactionRoutingDataSource dataSource
     * @return LazyConnectionDataSourceProxy
     */
    @Bean
    @ConditionalOnBean(name = "readOnlyDataSource")
    @Primary
    public DataSource routingDataSource(
        @Qualifier("transactionRoutingDataSource") DataSource transactionRoutingDataSource) {
        return new LazyConnectionDataSourceProxy(transactionRoutingDataSource);
    }

    /**
     * TransactionのReadOnly属性によって利用するDataSourceを切り替えるRouting設定。
     *
     * @param readOnlyDataSource ReadOnlyの場合に利用するDataSource
     * @param readWriteDataSource ReadOnlyではない場合に利用するDataSource
     * @return TransactionRoutingDataSource
     */
    @Bean
    @ConditionalOnBean(name = "readOnlyDataSource")
    public TransactionRoutingDataSource transactionRoutingDataSource(
        @Qualifier("readOnlyDataSource") DataSource readOnlyDataSource,
        @Qualifier("readWriteDataSource") DataSource readWriteDataSource) {
        TransactionRoutingDataSource transactionRoutingDataSource = new TransactionRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(TransactionRoutingDataSource.READ_WRITE, readWriteDataSource);
        dataSourceMap.put(TransactionRoutingDataSource.READ_ONLY, readOnlyDataSource);

        transactionRoutingDataSource.setTargetDataSources(dataSourceMap);
        transactionRoutingDataSource.setDefaultTargetDataSource(readOnlyDataSource);
        return transactionRoutingDataSource;
    }

    /**
     * トランザクション設定。
     *
     * @param dataSource dataSource
     * @return TransactionInterceptor
     */
    @Bean
    public TransactionInterceptor txAdvice(DataSource dataSource) {
        var requiredTx = new RuleBasedTransactionAttribute();
        // 複数DataSourceを利用する場合、requiredTx.setReadOnly(true)に変更することが望ましい。
        requiredTx.setReadOnly(false);
        List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
        rollbackRules.add(new RollbackRuleAttribute(Exception.class));
        rollbackRules.add(new NoRollbackRuleAttribute(NoRollbackBusinessFailureException.class));
        requiredTx.setRollbackRules(rollbackRules);
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        Map<String, TransactionAttribute> txMap = new HashMap<>();
        txMap.put("*", requiredTx);
        var source = new NameMatchTransactionAttributeSource();
        source.setNameMap(txMap);
        var txManager = new DataSourceTransactionManager(dataSource);
        return new TransactionInterceptor((TransactionManager) txManager, source);
    }

    /**
     * トランザクションのPointcut設定。{@link org.springframework.stereotype.Service}アノテーションが付与されているクラスのPublicメソッドが自動的にTransactionalとなる。
     *
     * @param txAdvice txAdvice
     * @return DefaultPointcutAdvisor
     */
    @Bean
    public DefaultPointcutAdvisor txAdvisor(TransactionInterceptor txAdvice) {
        var txAdvisor = new DefaultPointcutAdvisor();
        txAdvisor.setAdvice(txAdvice);
        var pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@within(org.springframework.stereotype.Service) "
            + "and !@annotation(org.springframework.transaction.annotation.Transactional) "
            + "and !@target(org.springframework.transaction.annotation.Transactional)");
        txAdvisor.setPointcut(pointcut);
        txAdvisor.setOrder(10);
        return txAdvisor;
    }
}
