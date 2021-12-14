package com.example.txpoc;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Event representing initial creation of something. "Something" is not
 * yet stored to the DB.
 */
public class SomethingCreated {
    public SomethingCreated() {
        this.id = counter.incrementAndGet();
    }

    public int getId() {
        return id;
    }

    private final int id;
    private static final AtomicInteger counter = new AtomicInteger(0);
}
