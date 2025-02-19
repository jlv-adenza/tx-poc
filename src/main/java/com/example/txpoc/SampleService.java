package com.example.txpoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SampleService {

    private static final Logger log = LoggerFactory.getLogger(SampleService.class);

    private final CacheOverlayConsumer consumer;
    private final ApplicationEventPublisher publisher;

    public SampleService(CacheOverlayConsumer consumer, ApplicationEventPublisher publisher) {
        this.consumer = consumer;
        this.publisher = publisher;
    }

    /**
     * Method will be executed within existing transaction or Spring
     * will start a new transaction if none exists. It utilizes Spring
     * application event publishing mechanism to notify about important
     * processing steps. This allows decoupled listeners to take actions
     * upon completion of the transaction.
     *
     * See {@link Transactional} documentation for configuration
     * parameter details.
     *
     * @param shouldFail true when method should emulate an error within
     *                   the transaction
     */
    @Transactional
    public void doInTransaction(boolean shouldFail) {
        log.info("STARTING BUSINESS TRANSACTION");
        publisher.publishEvent(new SomethingCreated());
        log.info("STORING SOMETHING TO DB");
        publisher.publishEvent(new SomethingStored());

        consumer.doSomethingWithCacheOverlay();

        try {
            if (shouldFail) {
                throw new RuntimeException("OOPS");
            }
        } finally {
            log.info("ENDING BUSINESS TRANSACTION");
        }
    }

}
