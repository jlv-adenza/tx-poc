package com.example.txpoc;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.time.Clock;

public class MyTransactionInterceptor extends TransactionInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MyTransactionInterceptor.class);
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long start = System.currentTimeMillis();
        log.info("begin");
        Object res = super.invoke(invocation);
        long end = System.currentTimeMillis();
        long duration = end - start;
        log.info("end: duration = {} milliseconds", duration);
        return res;
    }
}
