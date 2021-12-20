package com.example.txpoc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.annotation.PostConstruct;

@SpringBootTest
public class TxManagementTests {


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SampleService svc;

    @Autowired
    PlatformTransactionManager txManager;

    @Bean("TransactionInterceptor")
    @DependsOn("TransactionManager")
    public TransactionInterceptor transactionInterceptor() {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(txManager);
        return interceptor;
    }

    @PostConstruct
    public void postConstruct() {
    }

    @BeforeTestMethod
    public void before() {
        // Removing any information about last transaction status
        TransactionChecker.clearTransactionStatus();
    }

    @AfterTestMethod
    public void after() {
        // Removing any information about last transaction status
        TransactionChecker.clearTransactionStatus();
    }
    /**
     * See output logs in console for processing details. Note how
     * {@link SomethingStored} event is processed only after transaction has
     * been committed, even though in the {@link SampleService} code it
     * is published way ahead.
     */
    @Test
    public void shouldHandleSuccessfulTx() {
        Assertions.assertDoesNotThrow(() -> svc.doInTransaction(false));
        Assertions.assertEquals(TransactionChecker.TransactionStatus.COMMITTED, TransactionChecker.getTransactionStatus());
    }

    /**
     * See output logs in console for processing details. Note that the
     * transaction is rolled back and there is no cache overlay flushing or
     * message publishing as a result.
     */
    @Test
    public void shouldHandleFailedTx() {
        Assertions.assertThrows(RuntimeException.class, () -> svc.doInTransaction(true));
        Assertions.assertEquals(TransactionChecker.TransactionStatus.ROLLED_BACK, TransactionChecker.getTransactionStatus());
    }

}
