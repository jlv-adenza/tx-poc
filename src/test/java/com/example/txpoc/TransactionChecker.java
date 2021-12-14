package com.example.txpoc;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * This test class allows to provide a status about the last transaction
 * related to current thread
 * <p>
 *     We use activity on TCache to determine the status of the current transaction,
 *     of the transaction that was just completed.
 * </p>
 * <p>
 *     The information is stored into a thread local storage
 * </p>
 */
@Component
public class TransactionChecker {
    /**
     * A new TCache instance has been created
     * <p>
     *     We declare that we are in transaction in progress
     * </p>
     * @param event
     */
    @EventListener
    public void onTCacheCreation(TCacheCreated event) {
        transactionInfo.set(TransactionStatus.STARTED);
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
        transactionInfo.set(TransactionStatus.COMMITTED);
    }

    /**
     * Will be called by Spring only after the transaction in which this
     * event was generated has been rolled back. Can be used to
     * clean the local overlay up, assuming the failure scenario.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void afterRollback(TCacheCreated event) {
        transactionInfo.set(TransactionStatus.ROLLED_BACK);
    }

    /**
     *
     * @return last or current transaction status
     */
    public static TransactionStatus getTransactionStatus() {
        return transactionInfo.get();
    }

    /**
     * Indicate the last or current transaction status
     * @param status
     */
    public static void setTransactionStatus(TransactionStatus status) {
        TransactionChecker.transactionInfo.set(status);
    }

    /**
     * Clear any information about last transaction
     */
    public static void clearTransactionStatus() {
        TransactionChecker.transactionInfo.remove();
    }

    private static ThreadLocal<TransactionStatus> transactionInfo = ThreadLocal.withInitial(() -> TransactionStatus.NONE);

    public static enum TransactionStatus {
        /**
         * No transaction started as far as we know
         */
        NONE,
        /**
         * We know a transaction has started because a TCacheCreated event
         * has been listened at (see
         */
        STARTED,
        /**
         * The last transaction has been rolled-back
         */
        ROLLED_BACK,
        /**
         * The last transaction has been committed
         */
        COMMITTED;
    }

}
