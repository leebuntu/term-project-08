package com.leebuntu.server.db.core.transaction;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionManager {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition transactionCompleted = lock.newCondition();

    private Thread transactionOwnerThread = null;

    public void beginTransaction() {
        lock.lock();
        try {
            while (transactionOwnerThread != null) {
                transactionCompleted.await();
            }
            transactionOwnerThread = Thread.currentThread();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void endTransaction() {
        lock.lock();
        try {
            ensureTransactionOwner();
            transactionOwnerThread = null;
            transactionCompleted.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void waitForTransaction() {
        lock.lock();
        try {
            while (transactionOwnerThread != null && !transactionOwnerThread.equals(Thread.currentThread())) {
                transactionCompleted.await();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    private void ensureTransactionOwner() {
        if (!Thread.currentThread().equals(transactionOwnerThread)) {
            throw new IllegalStateException("Current thread does not own the transaction.");
        }
    }
}