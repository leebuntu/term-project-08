package com.leebuntu.manager;

import com.leebuntu.common.banking.BankingResult;
import com.leebuntu.common.banking.BankingResult.BankingResultType;
import com.leebuntu.common.banking.account.Account;
import com.leebuntu.common.banking.customer.Customer;
import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.common.banking.dto.request.auth.Login;
import com.leebuntu.common.banking.dto.request.banking.CreateAccount;
import com.leebuntu.common.banking.dto.request.banking.RemoveAccount;
import com.leebuntu.common.banking.dto.request.banking.ViewAccount;
import com.leebuntu.common.banking.dto.request.customer.CreateCustomer;
import com.leebuntu.common.banking.dto.request.customer.RemoveCustomer;
import com.leebuntu.common.banking.dto.response.banking.Accounts;
import com.leebuntu.common.banking.dto.response.customer.Customers;
import com.leebuntu.manager.communication.Connector;

public class BankManagerConnector {
    private static Connector connector = new Connector("bank.leebuntu.com", 8080);

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

    public static BankingResult getAccountsByCustomerId(String token, int customerId) {
        ViewAccount viewAccount = new ViewAccount(customerId);
        if (!connector.send("/admin/accounts/get", token, viewAccount)) {
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

    public static BankingResult deleteCustomer(String token, int userId) {
        RemoveCustomer removeCustomer = new RemoveCustomer(userId);
        if (!connector.send("/admin/customers/delete", token, removeCustomer)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        Response response = new Response();
        if (!connector.receiveAndBind(response)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        if (response.getStatus() == Status.SUCCESS) {
            return new BankingResult(BankingResultType.SUCCESS, "고객 삭제 성공");
        } else {
            return new BankingResult(BankingResultType.FAILED, response.getMessage());
        }
    }

    public static BankingResult deleteAccount(String token, String accountNumber) {
        RemoveAccount removeAccount = new RemoveAccount(accountNumber);
        if (!connector.send("/admin/accounts/delete", token, removeAccount)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        Response response = new Response();
        if (!connector.receiveAndBind(response)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        if (response.getStatus() == Status.SUCCESS) {
            return new BankingResult(BankingResultType.SUCCESS, "계좌 삭제 성공");
        } else {
            return new BankingResult(BankingResultType.FAILED, response.getMessage());
        }
    }

    public static BankingResult updateAccount(String token, Account account) {
        CreateAccount request = new CreateAccount(account);
        if (!connector.send("/admin/accounts/update", token, request)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        Response response = new Response();
        if (!connector.receiveAndBind(response)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        if (response.getStatus() == Status.SUCCESS) {
            return new BankingResult(BankingResultType.SUCCESS, "계좌 업데이트 성공");
        } else {
            return new BankingResult(BankingResultType.FAILED, response.getMessage());
        }
    }

    public static BankingResult updateCustomer(String token, Customer customer) {
        CreateCustomer createCustomer = new CreateCustomer(customer);
        if (!connector.send("/admin/customers/update", token, createCustomer)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        Response response = new Response();
        if (!connector.receiveAndBind(response)) {
            return new BankingResult(BankingResultType.INTERNAL_ERROR, "서버에 연결할 수 없습니다.");
        }

        if (response.getStatus() == Status.SUCCESS) {
            return new BankingResult(BankingResultType.SUCCESS, "고객 업데이트 성공");
        } else {
            return new BankingResult(BankingResultType.FAILED, response.getMessage());
        }
    }
}
