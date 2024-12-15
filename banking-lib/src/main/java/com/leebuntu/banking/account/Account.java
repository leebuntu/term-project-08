package com.leebuntu.banking.account;

public class Account {
    protected int id;
    protected int customerId;
    protected String accountNumber;
    protected long totalBalance;
    protected long availableBalance;
    protected long openDate;
    protected AccountType accountType;

    protected String linkedSavingsAccountNumber;
    protected double interestRate;
    protected long maxTransferAmountToChecking;

    public Account() {
    }

    public Account(int id, int customerId, String accountNumber, long totalBalance, long availableBalance,
            long openDate,
            AccountType accountType, String linkedSavingsAccountNumber, double interestRate,
            long maxTransferAmountToChecking) {
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

    public long getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(long totalBalance) {
        this.totalBalance = totalBalance;
    }

    public long getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(long availableBalance) {
        this.availableBalance = availableBalance;
    }

    public long getOpenDate() {
        return openDate;
    }

    public void setOpenDate(long openDate) {
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

    public long getMaxTransferAmountToChecking() {
        return maxTransferAmountToChecking;
    }

    public void setMaxTransferAmountToChecking(long maxTransferAmountToChecking) {
        this.maxTransferAmountToChecking = maxTransferAmountToChecking;
    }

    public boolean validate() {
        if (this.customerId < 0) {
            return false;
        }

        if (this.accountNumber == null || this.accountNumber.isEmpty() || this.accountNumber.length() != 9) {
            return false;
        }

        if (this.totalBalance < 0) {
            return false;
        }

        if (this.availableBalance < 0) {
            return false;
        }

        if (this.accountType == null) {
            return false;
        }

        switch (this.accountType) {
            case CHECKING:
                if (!this.linkedSavingsAccountNumber.isEmpty() && this.linkedSavingsAccountNumber.length() != 9) {
                    return false;
                }
                break;
            case SAVINGS:
                if (this.interestRate < 0) {
                    return false;
                }
                if (this.maxTransferAmountToChecking < 0) {
                    return false;
                }
                break;
        }

        return true;
    }
}
