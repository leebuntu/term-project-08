package com.leebuntu;

import com.leebuntu.banking.BankingResult;
import com.leebuntu.banking.BankingResult.BankingResultType;
import com.leebuntu.banking.Transaction;
import com.leebuntu.banking.account.Account;
import com.leebuntu.communication.Connector;
import com.leebuntu.communication.dto.Response;
import com.leebuntu.communication.dto.enums.Status;
import com.leebuntu.communication.dto.request.auth.Login;
import com.leebuntu.communication.dto.request.banking.DepositWithdraw;
import com.leebuntu.communication.dto.request.banking.GetTransactions;
import com.leebuntu.communication.dto.request.banking.Transfer;
import com.leebuntu.communication.dto.response.banking.Accounts;
import com.leebuntu.communication.dto.response.banking.Transactions;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class BankConnector {
	private static Connector connector = new Connector("localhost", 8080);

	public static BankingResult login(String id, String password) {
		Login login = new Login(id, password);

		if (!connector.send("/login", null, login)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		Response response = new Response();
		if (!connector.receiveAndBind(response)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		if (response.getStatus() == Status.SUCCESS) {
			return new BankingResult(BankingResultType.SUCCESS, "로그인 성공", response.getMessage());
		} else {
			return new BankingResult(BankingResultType.FAILED, response.getMessage());
		}
	}

	public static BankingResult getAccounts(String token) {
		if (!connector.send("/banking/account/get", token, null)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		Accounts accounts = new Accounts();
		if (!connector.receiveAndBind(accounts)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		return new BankingResult(BankingResultType.SUCCESS, "계좌 조회 성공", accounts.getAccounts());
	}

	public static BankingResult getFormattedAccounts(String token) {
		BankingResult result = getAccounts(token);

		if (result.getType() != BankingResultType.SUCCESS) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		return new BankingResult(BankingResultType.SUCCESS, "계좌 조회 성공",
				((List<Account>) result.getData()).stream().map(Account::getAccountNumber)
						.collect(Collectors.toList()));
	}

	public static BankingResult transfer(String token, String accountNumber, String receiverAccountNumber,
			Long amount) {
		Transfer transfer = new Transfer(accountNumber, amount, receiverAccountNumber);
		if (!connector.send("/banking/account/transfer", token, transfer)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		Response response = new Response();
		if (!connector.receiveAndBind(response)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		if (response.getStatus() == Status.SUCCESS) {
			return new BankingResult(BankingResultType.SUCCESS, "송금 성공");
		} else {
			return new BankingResult(BankingResultType.FAILED, response.getMessage());
		}

	}

	public static BankingResult deposit(String token, String accountNumber, Long amount) {
		DepositWithdraw depositWithdraw = new DepositWithdraw(accountNumber, amount);
		if (!connector.send("/banking/account/deposit", token, depositWithdraw)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		Response response = new Response();
		if (!connector.receiveAndBind(response)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		if (response.getStatus() == Status.SUCCESS) {
			return new BankingResult(BankingResultType.SUCCESS, "입금 성공");
		} else {
			return new BankingResult(BankingResultType.FAILED, response.getMessage());
		}

	}

	public static BankingResult withdraw(String token, String accountNumber, Long amount) {
		DepositWithdraw depositWithdraw = new DepositWithdraw(accountNumber, amount);
		if (!connector.send("/banking/account/withdraw", token, depositWithdraw)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		Response response = new Response();
		if (!connector.receiveAndBind(response)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		if (response.getStatus() == Status.SUCCESS) {
			return new BankingResult(BankingResultType.SUCCESS, "출금 성공");
		} else {
			return new BankingResult(BankingResultType.FAILED, response.getMessage());
		}

	}

	public static BankingResult getTransactions(String token, String accountNumber) {
		GetTransactions getTransactions = new GetTransactions(accountNumber);
		if (!connector.send("/banking/account/transactions", token, getTransactions)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		Transactions transactions = new Transactions();
		if (!connector.receiveAndBind(transactions)) {
			return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
		}

		return new BankingResult(BankingResultType.SUCCESS, "거래 조회 성공", transactions.getTransactions());
	}
}
