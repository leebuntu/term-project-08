package com.leebuntu.banking;

public class Transaction {
    private int senderId;
    private int receiverId;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private long amount;
    private long date;

    public Transaction(int senderId, int receiverId, String senderAccountNumber, String receiverAccountNumber,
            long amount, long date) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
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

    public long getAmount() {
        return amount;
    }

    public long getDate() {
        return date;
    }

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }
}
