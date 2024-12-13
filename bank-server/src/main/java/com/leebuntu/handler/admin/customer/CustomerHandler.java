package com.leebuntu.handler.admin.customer;


import com.leebuntu.banking.customer.Customer;
import com.leebuntu.banking.customer.CustomerType;
import com.leebuntu.communication.dto.Response;
import com.leebuntu.communication.dto.enums.Status;
import com.leebuntu.communication.dto.request.customer.CreateCustomer;
import com.leebuntu.communication.dto.request.customer.RemoveCustomer;
import com.leebuntu.communication.dto.response.customer.Customers;
import com.leebuntu.communication.router.ContextHandler;
import com.leebuntu.db.core.Database;
import com.leebuntu.db.core.DatabaseManager;
import com.leebuntu.db.query.QueryResult;
import com.leebuntu.db.query.enums.QueryStatus;
import com.leebuntu.handler.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CustomerHandler {
    private static final Database db = DatabaseManager.getDB("customers");

    public static ContextHandler createCustomer() {
        return (context) -> {
            if (!Utils.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }

            CreateCustomer request = new CreateCustomer();

            if (context.bind(request)) {
                String query = "INSERT INTO user customer_type, customer_id, password";
                QueryResult result = db.execute(query, CustomerType.CUSTOMER.ordinal(), request.getCustomerId(),
                        request.getPassword());

                int lastInsertId = (int) result.getLastInsertId();

                query = "INSERT INTO user_info id, name, address, phone, credit_score";
                result = db.execute(query, lastInsertId, request.getName(), request.getAddress(), request.getPhone(),
                        750);

                if (result.getQueryStatus() == QueryStatus.SUCCESS) {
                    context.reply(new Response(Status.SUCCESS, "Customer created successfully"));
                } else {
                    context.reply(new Response(Status.FAILED, "Failed to create customer"));
                }
            } else {
                context.reply(new Response(Status.FAILED, "Failed to create customer"));
            }
        };
    }

    public static ContextHandler getCustomers() {
        return (context) -> {
            if (!Utils.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }

            String query = "SELECT * FROM user";
            QueryResult result = db.execute(query);
            if (result.getQueryStatus() == QueryStatus.SUCCESS) {
                List<Customer> customers = new ArrayList<>();
                while (result.next()) {
                    List<Object> row = result.getCurrentRow();
                    int id = (int) row.get(0);
                    int customerType = (int) row.get(1);
                    String customerId = (String) row.get(2);

                    if (customerType != CustomerType.CUSTOMER.ordinal()) {
                        continue;
                    }

                    query = "SELECT * FROM user_info WHERE id = ?";
                    QueryResult infoResult = db.execute(query, id);
                    if (infoResult.getQueryStatus() != QueryStatus.SUCCESS) {
                        continue;
                    }

                    List<Object> userInfoRow = infoResult.getCurrentRow();
                    String name = (String) userInfoRow.get(1);
                    String address = (String) userInfoRow.get(2);
                    String phone = (String) userInfoRow.get(3);
                    int creditScore = (int) userInfoRow.get(4);

                    Customer customer = new Customer(id, name, customerId, address, phone, creditScore);
                    customers.add(customer);
                }
                context.reply(new Customers(Status.SUCCESS, "Customers fetched successfully", customers));
            } else {
                context.reply(new Response(Status.FAILED, "Failed to get customers"));
            }
        };
    }

    public static ContextHandler deleteCustomer() {
        return (context) -> {
            if (!Utils.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }

            RemoveCustomer request = new RemoveCustomer();

            if (context.bind(request)) {
                String query = "DELETE FROM user WHERE id = ?";
                QueryResult result = db.execute(query, request.getId());
                if (result.getQueryStatus() == QueryStatus.SUCCESS) {
                    context.reply(new Response(Status.SUCCESS, "Customer deleted successfully"));
                } else {
                    context.reply(new Response(Status.FAILED, "Customer deleted failed"));

                }
            } else {
                context.reply(new Response(Status.FAILED, "Failed to delete customer"));
            }
        };
    }
}
