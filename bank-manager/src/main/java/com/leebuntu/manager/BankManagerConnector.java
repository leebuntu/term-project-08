package com.leebuntu.manager;

import com.leebuntu.banking.BankingResult;
import com.leebuntu.banking.BankingResult.BankingResultType;
import com.leebuntu.banking.account.Account;
import com.leebuntu.banking.customer.Customer;
import com.leebuntu.common.communication.Connector;
import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.banking.dto.request.auth.Login;
import com.leebuntu.banking.dto.request.banking.CreateAccount;
import com.leebuntu.banking.dto.request.customer.CreateCustomer;
import com.leebuntu.banking.dto.response.banking.Accounts;
import com.leebuntu.banking.dto.response.customer.Customers;

public class BankManagerConnector {
    private static Connector connector = new Connector("localhost", 8080);

    public static BankingResult getCustomers(String token) {
        if (!connector.send("/admin/customers/get", token, null)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        Customers customers = new Customers();
        if (!connector.receiveAndBind(customers)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        return new BankingResult(BankingResultType.SUCCESS, "고객 조회 성공", customers.getCustomers());
    }

    public static BankingResult getAccounts(String token) {
        if (!connector.send("/admin/accounts/get/all", token, null)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        Accounts accounts = new Accounts();
        if (!connector.receiveAndBind(accounts)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        return new BankingResult(BankingResultType.SUCCESS, "계좌 조회 성공", accounts.getAccounts());
    }

    public static BankingResult login(String username, String password) {
        Login login = new Login(username, password);

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

    public static BankingResult createCustomer(String token, String customerId, String password, String name,
            String address, String phone) {
        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setPassword(password);
        customer.setName(name);
        customer.setAddress(address);
        customer.setPhone(phone);

        CreateCustomer createCustomer = new CreateCustomer(customer);
        if (!connector.send("/admin/customers/create", token, createCustomer)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        Response response = new Response();
        if (!connector.receiveAndBind(response)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        if (response.getStatus() == Status.SUCCESS) {
            return new BankingResult(BankingResultType.SUCCESS, "고객 생성 성공");
        } else {
            return new BankingResult(BankingResultType.FAILED, response.getMessage());
        }
    }

    public static BankingResult createCheckingAccount(String token, Account account) {
        CreateAccount createCheckingAccount = new CreateAccount(account);
        if (!connector.send("/admin/accounts/create", token, createCheckingAccount)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        Response response = new Response();
        if (!connector.receiveAndBind(response)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        if (response.getStatus() == Status.SUCCESS) {
            return new BankingResult(BankingResultType.SUCCESS, "계좌 생성 성공");
        } else {
            return new BankingResult(BankingResultType.FAILED, response.getMessage());
        }
    }

    public static BankingResult createSavingsAccount(String token, Account account) {
        CreateAccount createSavingsAccount = new CreateAccount(account);

        if (!connector.send("/admin/accounts/create", token, createSavingsAccount)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        Response response = new Response();
        if (!connector.receiveAndBind(response)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        if (response.getStatus() == Status.SUCCESS) {
            return new BankingResult(BankingResultType.SUCCESS, "계좌 생성 성공");
        } else {
            return new BankingResult(BankingResultType.FAILED, response.getMessage());
        }
    }
}
