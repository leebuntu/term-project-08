package com.leebuntu;

import com.leebuntu.banking.customer.CustomerType;
import com.leebuntu.communication.jwt.JWTMiddleware;
import com.leebuntu.communication.router.Router;
import com.leebuntu.db.core.Database;
import com.leebuntu.db.core.DatabaseManager;
import com.leebuntu.handler.admin.banking.AccountHandler;
import com.leebuntu.handler.admin.customer.CustomerHandler;
import com.leebuntu.handler.user.customer.LoginHandler;

import java.io.IOException;

class ServerMain {
    private static void buildDB() {
        try {
            DBBuild.buildUsersDB();
            DBBuild.buildAccountsDB();
            DBBuild.buildTransactionsDB();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        registerAdmin();
    }

    private static void registerAdmin() {
        String query = "INSERT INTO user customer_type, customer_id, password";
        Database db = DatabaseManager.getDB("customers");
        db.execute(query, CustomerType.ADMIN.ordinal(), "admin", "admin123");
    }

    public static void main(String[] args) throws Exception {

        // buildDB();

        Router router = new Router(8080);

        router.addRoute("/admin/customers/get", CustomerHandler.getCustomers(), JWTMiddleware.getJWTMiddleware());
        router.addRoute("/admin/customers/create", CustomerHandler.createCustomer(), JWTMiddleware.getJWTMiddleware());
        router.addRoute("/admin/customers/delete", CustomerHandler.deleteCustomer(), JWTMiddleware.getJWTMiddleware());

        router.addRoute("/admin/accounts/create", AccountHandler.createAccount(), JWTMiddleware.getJWTMiddleware());
        router.addRoute("/admin/accounts/get", AccountHandler.getAccount(), JWTMiddleware.getJWTMiddleware());
        router.addRoute("/admin/accounts/get/all", AccountHandler.getAllAccounts(), JWTMiddleware.getJWTMiddleware());

        router.addRoute("/login", LoginHandler.getLoginHandler());

        router.addRoute("/banking/account/withdraw",
                com.leebuntu.handler.user.banking.AccountHandler.withdraw(),
                JWTMiddleware.getJWTMiddleware());
        router.addRoute("/banking/account/deposit",
                com.leebuntu.handler.user.banking.AccountHandler.deposit(),
                JWTMiddleware.getJWTMiddleware());
        router.addRoute("/banking/account/transfer",
                com.leebuntu.handler.user.banking.AccountHandler.transfer(),
                JWTMiddleware.getJWTMiddleware());
        router.addRoute("/banking/account/get",
                com.leebuntu.handler.user.banking.AccountHandler.getAccounts(),
                JWTMiddleware.getJWTMiddleware());

        router.addRoute("/banking/transaction/get",
                com.leebuntu.handler.user.banking.TransactionHandler.getTransactions(),
                JWTMiddleware.getJWTMiddleware());

        router.start();
    }
}
