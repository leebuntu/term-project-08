package com.leebuntu.common.communication.dto.request.banking;


import com.leebuntu.common.communication.dto.Payload;

public class TakeOutLoan extends Payload<TakeOutLoan> {
	private String accountNumber;
	private Long amount;

	public TakeOutLoan(String accountNumber, Long amount) {
		super();
		this.accountNumber = accountNumber;
		this.amount = amount;
	}

	public TakeOutLoan() {
		super();
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public Long getAmount() {
		return amount;
	}

	@Override
	public TakeOutLoan fromJson(String json) {
		TakeOutLoan takeOutLoan = super.fromJson(json);
		this.accountNumber = takeOutLoan.accountNumber;
		this.amount = takeOutLoan.amount;
		return this;
	}
}
