package com.leebuntu.banking.account;

public class Account {
    protected int id;
    protected int customerId;
    protected String accountNumber;
    protected Long totalBalance;
    protected Long availableBalance;
    protected Long openDate;
    protected AccountType accountType;

    protected int linkedSavingsAccountNumber;
    protected double interestRate;
    protected Long maxTransferAmountToChecking;

    public Account() {
    }

    public Account(String accountNumber, Long totalBalance, Long availableBalance, AccountType accountType) {
        this.accountNumber = accountNumber;
        this.totalBalance = totalBalance;
        this.availableBalance = availableBalance;
        this.accountType = accountType;
    }

    public Account(int customerId, String accountNumber, Long totalBalance, Long availableBalance, Long openDate,
            AccountType accountType, int linkedSavingsAccountNumber) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.totalBalance = totalBalance;
        this.availableBalance = availableBalance;
        this.openDate = openDate;
        this.accountType = accountType;
        this.linkedSavingsAccountNumber = linkedSavingsAccountNumber;
    }

    public Account(int customerId, String accountNumber, Long totalBalance, Long availableBalance, Long openDate,
            AccountType accountType, double interestRate, Long maxTransferAmountToChecking) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.totalBalance = totalBalance;
        this.availableBalance = availableBalance;
        this.openDate = openDate;
        this.accountType = accountType;
        this.interestRate = interestRate;
        this.maxTransferAmountToChecking = maxTransferAmountToChecking;
    }

    public Account(int id, int customerId, String accountNumber, Long totalBalance, Long availableBalance,
            Long openDate,
            AccountType accountType, int linkedSavingsAccountNumber, double interestRate,
            Long maxTransferAmountToChecking) {
        this.id = id;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.totalBalance = totalBalance;
        this.availableBalance = availableBalance;
        this.openDate = openDate;
        this.accountType = accountType;
        this.linkedSavingsAccountNumber = linkedSavingsAccountNumber;
        this.interestRate = interestRate;
        this.maxTransferAmountToChecking = maxTransferAmountToChecking;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Long getTotalBalance() {
        return totalBalance;
    }

    public Long getAvailableBalance() {
        return availableBalance;
    }

    public Long getOpenDate() {
        return openDate;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public int getLinkedSavingsAccountNumber() {
        return linkedSavingsAccountNumber;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public Long getMaxTransferAmountToChecking() {
        return maxTransferAmountToChecking;
    }
}
