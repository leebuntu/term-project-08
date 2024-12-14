package com.leebuntu.server.db.core;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionManager {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition transactionCompleted = lock.newCondition();
    private final AtomicBoolean isTransactionActive = new AtomicBoolean(false);

    public void beginTransaction() {
        lock.lock();
        try {
            while (isTransactionActive.get()) {
                transactionCompleted.await();
            }
            isTransactionActive.set(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void commitTransaction() {
        lock.lock();
        try {
            isTransactionActive.set(false);
            transactionCompleted.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void rollbackTransaction() {
        lock.lock();
        try {
            isTransactionActive.set(false);
            transactionCompleted.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void waitIfTransactionActive() {
        if (isTransactionActive.get()) {
            lock.lock();
            try {
                while (isTransactionActive.get()) {
                    transactionCompleted.await();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }
}