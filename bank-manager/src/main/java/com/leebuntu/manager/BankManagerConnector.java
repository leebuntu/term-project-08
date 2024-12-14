package com.leebuntu.manager;

import com.leebuntu.common.banking.account.Account;
import com.leebuntu.common.banking.customer.Customer;
import com.leebuntu.common.communication.Connector;
import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.common.communication.dto.request.auth.Login;
import com.leebuntu.common.communication.dto.request.banking.CreateAccount;
import com.leebuntu.common.communication.dto.request.customer.CreateCustomer;
import com.leebuntu.common.communication.dto.response.banking.Accounts;
import com.leebuntu.common.communication.dto.response.customer.Customers;

import java.util.List;

public class BankManagerConnector {
    private static Connector connector = new Connector("localhost", 8080);

    public static List<Customer> getCustomers(String token) {
        connector.send("/admin/customers/get", token, null);

        Customers customers = new Customers();
        connector.receiveAndBind(customers);

        return customers.getCustomers();
    }

    public static List<Account> getAccounts(String token) {
        connector.send("/admin/accounts/get/all", token, null);

        Accounts accounts = new Accounts();
        connector.receiveAndBind(accounts);

        return accounts.getAccounts();
    }

    public static String login(String username, String password) {
        Login login = new Login(username, password);

        connector.send("/login", null, login);

        Response response = new Response();
        connector.receiveAndBind(response);

        if (response.getStatus() == Status.SUCCESS) {
            return response.getMessage();
        } else {
            return null;
        }
    }

    public static boolean createCustomer(String token, String customerId, String name, String password, String address,
            String phone) {
        CreateCustomer createCustomer = new CreateCustomer(customerId, name, password, address, phone);
        connector.send("/admin/customers/create", token, createCustomer);

        Response response = new Response();
        connector.receiveAndBind(response);
        if (response.getStatus() == Status.SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean createCheckingAccount(String token, Account account) {
        CreateAccount createCheckingAccount = new CreateAccount(account.getCustomerId(),
                account.getAccountNumber(), account.getTotalBalance(),
                account.getAvailableBalance(),
                account.getAccountType(), account.getLinkedSavingsAccountNumber());

        connector.send("/admin/accounts/create", token, createCheckingAccount);

        Response response = new Response();
        connector.receiveAndBind(response);
        if (response.getStatus() == Status.SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean createSavingsAccount(String token, Account account) {
        CreateAccount createSavingsAccount = new CreateAccount(account.getCustomerId(),
                account.getAccountNumber(), account.getTotalBalance(),
                account.getAvailableBalance(), account.getAccountType(),
                account.getInterestRate(), account.getMaxTransferAmountToChecking());

        connector.send("/admin/accounts/create", token, createSavingsAccount);

        Response response = new Response();
        connector.receiveAndBind(response);
        if (response.getStatus() == Status.SUCCESS) {
            return true;
        } else {
            return false;
        }
    }
}
