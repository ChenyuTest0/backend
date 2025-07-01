package com.accenture.acts.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * TransactionのReadOnly属性を読み取り、設定値によってDataSourceを切り替えるRoutingDataSource。複数DBを切り替える際に利用される。
 */
public class TransactionRoutingDataSource extends AbstractRoutingDataSource {

    /**
     * リーダーDBを特定するためのキー。
     */
    public static final String READ_ONLY = "READ_ONLY";

    /**
     * ライターDBを特定するためのキー。
     */
    public static final String READ_WRITE = "READ_WRITE";

    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? READ_ONLY : READ_WRITE;
    }

}
