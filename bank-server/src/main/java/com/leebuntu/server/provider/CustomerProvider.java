package com.leebuntu.server.provider;

import java.util.ArrayList;
import java.util.List;

import com.leebuntu.common.banking.customer.Customer;
import com.leebuntu.common.banking.customer.CustomerType;
import com.leebuntu.server.Constants;
import com.leebuntu.server.db.core.Database;
import com.leebuntu.server.db.core.DatabaseManager;
import com.leebuntu.server.db.query.QueryResult;
import com.leebuntu.server.db.query.enums.QueryStatus;

public class CustomerProvider {
    private static final Database customerDB = DatabaseManager.getDB(Constants.CUTOMER_DB_NAME);

    public static boolean isExistUser(int customerId) {
        String query = "SELECT id FROM user WHERE id = ?";
        QueryResult result = customerDB.execute(query, customerId);
        return result.getRowCount() > 0;
    }

    public static boolean isAdmin(int userId) {
        String query = "SELECT customer_type FROM user WHERE id = ?";
        QueryResult result = customerDB.execute(query, userId);
        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            return false;
        }

        return (int) result.getCurrentRow().get(0) == CustomerType.ADMIN.ordinal();
    }

    public static int login(String customerId, String password) {
        String query = "SELECT id, password FROM user WHERE customer_id = ?";
        QueryResult result = customerDB.execute(query, customerId);
        if (result.getRowCount() == 0) {
            return -1;
        }

        String dbPassword = result.getCurrentRow().get(1).toString();
        return dbPassword.equals(password) ? (int) result.getCurrentRow().get(0) : -1;
    }

    public static int createCustomer(Customer customer) {
        String query = "INSERT INTO user customer_type, customer_id, password";
        QueryResult result = customerDB.execute(query, CustomerType.CUSTOMER.ordinal(), customer.getCustomerId(),
                customer.getPassword());
        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            return -1;
        }

        int lastInsertId = (int) result.getLastInsertId();

        query = "INSERT INTO user_info id, name, address, phone, credit_score";
        result = customerDB.execute(query, lastInsertId, customer.getName(), customer.getAddress(), customer.getPhone(),
                750);
        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            return -1;
        }

        return lastInsertId;
    }

    public static List<Customer> getAllCustomers() {
        String query = "SELECT * FROM user";
        QueryResult result = customerDB.execute(query);
        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            return new ArrayList<>();
        }

        List<Customer> customers = new ArrayList<>();
        while (result.next()) {
            List<Object> row = result.getCurrentRow();
            int id = (int) row.get(0);
            int customerType = (int) row.get(1);
            String customerId = (String) row.get(2);
            String password = (String) row.get(3);

            if (customerType != CustomerType.CUSTOMER.ordinal()) {
                continue;
            }

            query = "SELECT * FROM user_info WHERE id = ?";
            QueryResult infoResult = customerDB.execute(query, id);
            if (infoResult.getQueryStatus() != QueryStatus.SUCCESS) {
                continue;
            }

            List<Object> userInfoRow = infoResult.getCurrentRow();
            String name = (String) userInfoRow.get(1);
            String address = (String) userInfoRow.get(2);
            String phone = (String) userInfoRow.get(3);
            int creditScore = (int) userInfoRow.get(4);

            customers.add(new Customer(id, name, customerId, password, address, phone, creditScore));
        }

        return customers;
    }

    public static Customer getCustomer(int customerId) {
        String query = "SELECT * FROM user_info WHERE id = ?";
        QueryResult result = customerDB.execute(query, customerId);
        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            return null;
        }

        Customer customer = new Customer();
        customer.setId((int) result.getCurrentRow().get(0));
        customer.setName((String) result.getCurrentRow().get(1));
        customer.setCustomerId((String) result.getCurrentRow().get(2));
        customer.setAddress((String) result.getCurrentRow().get(4));
        customer.setPhone((String) result.getCurrentRow().get(5));
        customer.setCreditScore((int) result.getCurrentRow().get(6));
        return customer;
    }

    public static boolean deleteCustomer(int customerId) {
        String query = "DELETE FROM user WHERE id = ?";
        QueryResult result = customerDB.execute(query, customerId);
        return result.getQueryStatus() == QueryStatus.SUCCESS;
    }

    public static boolean updateCustomer(Customer customer) {
        customerDB.beginTransaction();

        String query = "UPDATE FROM user password = ? WHERE id = ?";
        QueryResult result = customerDB.execute(query, customer.getPassword(), customer.getId());
        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            customerDB.endTransaction();
            return false;
        }

        query = "UPDATE FROM user_info name = ?, address = ?, phone = ?, credit_score = ? WHERE id = ?";
        result = customerDB.execute(query, customer.getName(), customer.getAddress(), customer.getPhone(),
                customer.getCreditScore(), customer.getId());
        if (result.getQueryStatus() != QueryStatus.SUCCESS) {
            customerDB.endTransaction();
            return false;
        }

        customerDB.endTransaction();

        return true;
    }
}
