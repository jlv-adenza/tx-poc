package com.example.txpoc;

import org.springframework.boot.autoconfigure.transaction.PlatformTransactionManagerCustomizer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@Component
public class MyPlatformTransactionMangerCustomizer implements PlatformTransactionManagerCustomizer<AbstractPlatformTransactionManager> {
    @Override
    public void customize(AbstractPlatformTransactionManager transactionManager) {
        transactionManager.setDefaultTimeout(300);
    }

}
