package com.leebuntu.banking;

public class Transaction {
    private int senderId;
    private int receiverId;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private Long amount;
    private Long date;

    public Transaction(int senderId, int receiverId, String senderAccountNumber, String receiverAccountNumber,
            Long amount, Long date) {
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

    public Long getAmount() {
        return amount;
    }

    public Long getDate() {
        return date;
    }

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }
}
