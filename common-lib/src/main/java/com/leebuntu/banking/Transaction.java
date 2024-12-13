package com.leebuntu.banking;

import java.time.Instant;

public class Transaction {
    private int senderId;
    private int receiverId;
    private int senderAccountId;
    private int receiverAccountId;
    private Long amount;
    private Instant date;

    public Transaction(int senderId, int receiverId, int senderAccountId, int receiverAccountId, Long amount,
            Instant date) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderAccountId = senderAccountId;
        this.receiverAccountId = receiverAccountId;
        this.amount = amount;
        this.date = date;
    }

    public Transaction() {
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public Long getAmount() {
        return amount;
    }

    public Instant getDate() {
        return date;
    }

    public int getSenderAccountId() {
        return senderAccountId;
    }

    public int getReceiverAccountId() {
        return receiverAccountId;
    }
}
