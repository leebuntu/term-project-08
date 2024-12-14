package com.leebuntu;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BankConnector {
	private static Connector connector;

	private static boolean tryConnect() {
		try {
			if (connector == null) {
				connector = new Connector("localhost", 8080);
				return true;
			} else {
				return true;
			}
		} catch (IOException e) {
			connector = null;
			return false;
		}
	}

	public static String login(String id, String password) {
		if (!tryConnect()) {
			return null;
		}

		try {
			Login login = new Login(id, password);

			connector.send("/login", null, login);

			Response response = new Response();
			connector.receiveAndBind(response);

			if (response.getStatus() == Status.SUCCESS) {
				return response.getMessage();
			} else {
				return null;
			}
		} catch (IOException e) {
			return null;
		}
	}

	public static List<Account> getAccounts(String token) {
		if (!tryConnect()) {
			return null;
		}

		try {
			connector.send("/banking/account/get", token, null);

			Accounts accounts = new Accounts();
			connector.receiveAndBind(accounts);

			return accounts.getAccounts();
		} catch (IOException e) {
			return null;
		}

	}

	public static List<String> getFormattedAccounts(String token) {
		if (!tryConnect()) {
			return null;
		}

		List<Account> accounts = getAccounts(token);

		return accounts.stream().map(Account::getAccountNumber).collect(Collectors.toList());
	}

	public static boolean transfer(String token, String accountNumber, String receiverAccountNumber, Long amount) {
		if (!tryConnect()) {
			return false;
		}

		try {
			Transfer transfer = new Transfer(accountNumber, amount, receiverAccountNumber);
			connector.send("/banking/account/transfer", token, transfer);

			Response response = new Response();
			connector.receiveAndBind(response);

			return response.getStatus() == Status.SUCCESS;
		} catch (IOException e) {
			return false;
		}

	}

	public static boolean deposit(String token, String accountNumber, Long amount) {
		if (!tryConnect()) {
			return false;
		}

		try {
			DepositWithdraw depositWithdraw = new DepositWithdraw(accountNumber, amount);
			connector.send("/banking/account/deposit", token, depositWithdraw);

			Response response = new Response();
			connector.receiveAndBind(response);

			return response.getStatus() == Status.SUCCESS;
		} catch (IOException e) {
			return false;
		}

	}

	public static boolean withdraw(String token, String accountNumber, Long amount) {
		if (!tryConnect()) {
			return false;
		}

		try {
			DepositWithdraw depositWithdraw = new DepositWithdraw(accountNumber, amount);
			connector.send("/banking/account/withdraw", token, depositWithdraw);

			Response response = new Response();
			connector.receiveAndBind(response);

			return response.getStatus() == Status.SUCCESS;
		} catch (IOException e) {
			return false;
		}

	}

	public static List<Transaction> getTransactions(String token, String accountNumber) {
		if (!tryConnect()) {
			return null;
		}

		try {
			GetTransactions getTransactions = new GetTransactions(accountNumber);
			connector.send("/banking/account/transactions", token, getTransactions);

			Transactions transactions = new Transactions();
			connector.receiveAndBind(transactions);

			return transactions.getTransactions();
		} catch (IOException e) {
			return null;
		}
	}
}
