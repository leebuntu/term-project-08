package com.leebuntu.common.communication.dto.request.banking;


import com.leebuntu.common.banking.account.AccountType;
import com.leebuntu.common.communication.dto.Payload;

public class CreateAccount extends Payload<CreateAccount> {
    private int customerId;
    private String accountNumber;
    private Long totalBalance;
    private Long availableBalance;
    private AccountType accountType;
    private int linkedSavingsAccountNumber;
    private double interestRate;
    private Long maxTransferAmountToChecking;

    public CreateAccount(int customerId, String accountNumber, Long totalBalance, Long availableBalance,
            AccountType accountType, int linkedSavingsAccountNumber) {
        super();
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.totalBalance = totalBalance;
        this.availableBalance = availableBalance;
        this.accountType = accountType;
        this.linkedSavingsAccountNumber = linkedSavingsAccountNumber;
    }

    public CreateAccount(int customerId, String accountNumber, Long totalBalance, Long availableBalance,
            AccountType accountType, double interestRate, Long maxTransferAmountToChecking) {
        super();
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.totalBalance = totalBalance;
        this.availableBalance = availableBalance;
        this.accountType = accountType;
        this.interestRate = interestRate;
        this.maxTransferAmountToChecking = maxTransferAmountToChecking;
    }

    public CreateAccount() {
        super();
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

    @Override
    public CreateAccount fromJson(String json) {
        CreateAccount createAccount = super.fromJson(json);
        this.customerId = createAccount.customerId;
        this.accountNumber = createAccount.accountNumber;
        this.totalBalance = createAccount.totalBalance;
        this.availableBalance = createAccount.availableBalance;
        this.accountType = createAccount.accountType;
        this.linkedSavingsAccountNumber = createAccount.linkedSavingsAccountNumber;
        this.interestRate = createAccount.interestRate;
        this.maxTransferAmountToChecking = createAccount.maxTransferAmountToChecking;
        return this;
    }
}
