package com.leebuntu.banking.account;

public class Account {
    protected int id;
    protected int customerId;
    protected String accountNumber;
    protected Long totalBalance;
    protected Long availableBalance;
    protected Long openDate;
    protected AccountType accountType;

    protected String linkedSavingsAccountNumber;
    protected double interestRate;
    protected Long maxTransferAmountToChecking;

    public Account() {
    }

    public Account(int id, int customerId, String accountNumber, Long totalBalance, Long availableBalance,
            Long openDate,
            AccountType accountType, String linkedSavingsAccountNumber, double interestRate,
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

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Long totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Long getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(Long availableBalance) {
        this.availableBalance = availableBalance;
    }

    public Long getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Long openDate) {
        this.openDate = openDate;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getLinkedSavingsAccountNumber() {
        return linkedSavingsAccountNumber;
    }

    public void setLinkedSavingsAccountNumber(String linkedSavingsAccountNumber) {
        this.linkedSavingsAccountNumber = linkedSavingsAccountNumber;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public Long getMaxTransferAmountToChecking() {
        return maxTransferAmountToChecking;
    }

    public void setMaxTransferAmountToChecking(Long maxTransferAmountToChecking) {
        this.maxTransferAmountToChecking = maxTransferAmountToChecking;
    }
}
