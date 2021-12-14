package com.example.txpoc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Event representing storage of something to the DB.
 */
public class SomethingStored {
    public SomethingStored(String cacheName) {
        this.cacheName = cacheName;
        this.operationId = counter.incrementAndGet();
    }

    public SomethingStored() {
        operationId = counter.incrementAndGet();
        this.cacheName = "";
    }

    public String getCacheName() {
        return cacheName;
    }

    public int getOperationId() {
        return operationId;
    }

    private final String cacheName;
    private final int operationId;
    private static final AtomicInteger counter = new AtomicInteger(0);
}
