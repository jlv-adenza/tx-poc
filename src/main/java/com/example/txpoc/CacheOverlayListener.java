package com.example.txpoc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Demo of a decoupled approach to a local cache overlay management in response
 * to events happening within a transaction.
 * <p>
 *     In our product, we have in-memory transactional cache.
 *     When data is added to a transactional cache, it is not added to the cache itself,
 *     but to an overlay of the cache called TCache. This overlay will be merged into the
 *     transactional cache upon commit, or discarded upon rollback
 * </p>
 * <p>
 *     <ul>
 *         <li>And event listener, uponTCacheCreation() is called whenever we create a new TCache on
 *         a given cache.</li>
 *         <li>a transaction event listener afterCommit(), is called for each created TCache upon commit</li>
 *         li>a transaction event listener afterRollback(), is called for each created TCache upon rollback</li>
 *     </ul>
 *
 *
 * </p>
 */
@Component
public class CacheOverlayListener {
    private static final Logger log = LoggerFactory.getLogger(CacheOverlayListener.class);

    /**
     * Will be called by Spring as soon as a TCache is created
     * regardless of the current status of the transaction. Can be used to
     * record TCache creation.
     */
    @EventListener
    public void onTCacheCreation(TCacheCreated event) {
        log.info("WITHIN TRANSACTION: Creating TCache = " + event.getCacheName());
    }

    /**
     * Will be called by Spring only after the transaction in which this
     * event was generated has been successfully committed. Can be used to
     * flush the local overlay to the main cache.
     *
     * Note that the AFTER_COMMIT phase is a default one for the annotation
     * and is specified here only for additional clarity.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(TCacheCreated event) {
        log.info("AFTER COMMIT: Flushing TCACHE = " + event.getCacheName());
    }

    /**
     * Will be called by Spring only after the transaction in which this
     * event was generated has been rolled back. Can be used to
     * clean the local overlay up, assuming the failure scenario.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void afterRollback(TCacheCreated event) {
        log.info("AFTER ROLLBACK: Clearing TCACHE = " + event.getCacheName());
    }

    /**
     * Will be called by Spring as soon as the specified even is published
     * regardless of the current status of the transaction. Can be used to
     * place newly created object into an overlay immediately available to
     * the subsequent calls.
     */
    @Deprecated @EventListener
    public void updateCacheOverlay(SomethingCreated event) {
        log.info("SOMETHING CREATED. UPDATING OVERLAY");
    }

    /**
     * Will be called by Spring only after the transaction in which this
     * event was generated has been successfully committed. Can be used to
     * flush the local overlay to the main cache.
     *
     * Note that the AFTER_COMMIT phase is a default one for the annotation
     * and is specified here only for additional clarity.
     */
    @Deprecated @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void flushCacheOverlay(SomethingStored event) {
        log.info("SOMETHING STORED TO DB. FLUSHING OVERLAY / operation id = " + event.getOperationId());
    }

    /**
     * Will be called by Spring only after the transaction in which this
     * event was generated has been rolled back. Can be used to
     * clean the local overlay up, assuming the failure scenario.
     */
    @Deprecated @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void cleanupCacheOverlay(SomethingStored event) {
        log.info("SOMETHING FAILED TO BE STORED TO DB. CLEANING UP OVERLAY / operation id = " + event.getOperationId());
    }

}
