package com.leebuntu;

import com.leebuntu.banking.account.Account;
import com.leebuntu.banking.customer.Customer;
import com.leebuntu.communication.Connector;
import com.leebuntu.communication.dto.Response;
import com.leebuntu.communication.dto.enums.Status;
import com.leebuntu.communication.dto.request.auth.Login;
import com.leebuntu.communication.dto.request.banking.CreateAccount;
import com.leebuntu.communication.dto.request.customer.CreateCustomer;
import com.leebuntu.communication.dto.response.banking.Accounts;
import com.leebuntu.communication.dto.response.customer.Customers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BankManagerConnector {
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

    public static List<Customer> getCustomers(String token) {
        if (!tryConnect()) {
            return null;
        }
        try {
            connector.send("/admin/customers/get", token, null);

            Customers customers = new Customers();
            connector.receiveAndBind(customers);

            return customers.getCustomers();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static List<Account> getAccounts(String token) {
        if (!tryConnect()) {
            return null;
        }

        try {
            connector.send("/admin/accounts/get/all", token, null);

            Accounts accounts = new Accounts();
            connector.receiveAndBind(accounts);

            return accounts.getAccounts();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static String login(String username, String password) {
        if (!tryConnect()) {
            return null;
        }

        try {
            Login login = new Login(username, password);

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

    public static boolean createCustomer(String token, String customerId, String name, String password, String address,
            String phone) {
        if (!tryConnect()) {
            return false;
        }

        try {
            CreateCustomer createCustomer = new CreateCustomer(customerId, name, password, address, phone);
            connector.send("/admin/customers/create", token, createCustomer);

            Response response = new Response();
            connector.receiveAndBind(response);
            if (response.getStatus() == Status.SUCCESS) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean createCheckingAccount(String token, Account account) {
        if (!tryConnect()) {
            return false;
        }

        try {
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
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean createSavingsAccount(String token, Account account) {
        if (!tryConnect()) {
            return false;
        }

        try {
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
        } catch (IOException e) {
            return false;
        }
    }
}
