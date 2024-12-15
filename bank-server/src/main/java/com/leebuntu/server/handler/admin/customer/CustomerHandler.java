package com.leebuntu.server.handler.admin.customer;

import com.leebuntu.banking.customer.Customer;
import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.banking.dto.request.customer.CreateCustomer;
import com.leebuntu.banking.dto.request.customer.RemoveCustomer;
import com.leebuntu.banking.dto.response.customer.Customers;
import com.leebuntu.common.communication.router.ContextHandler;
import com.leebuntu.server.provider.CustomerProvider;

import java.util.List;

public class CustomerHandler {

    public static ContextHandler createCustomer() {
        return (context) -> {
            if (!CustomerProvider.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }

            CreateCustomer request = new CreateCustomer();

            if (context.bind(request)) {
                Customer customer = new Customer();
                customer.setName(request.getCustomer().getName());
                customer.setCustomerId(request.getCustomer().getCustomerId());
                customer.setPassword(request.getCustomer().getPassword());
                customer.setAddress(request.getCustomer().getAddress());
                customer.setPhone(request.getCustomer().getPhone());

                int customerId = CustomerProvider.createCustomer(customer);
                if (customerId != -1) {
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
            if (!CustomerProvider.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }

            List<Customer> customers = CustomerProvider.getAllCustomers();
            if (customers.isEmpty()) {
                context.reply(new Response(Status.FAILED, "No customers found"));
            } else {
                context.reply(new Customers(Status.SUCCESS, "Customers fetched successfully", customers));
            }
        };
    }

    public static ContextHandler deleteCustomer() {
        return (context) -> {
            if (!CustomerProvider.isAdmin((int) context.getField("userId"))) {
                context.reply(new Response(Status.NOT_AUTHORIZED, "Unauthorized"));
                return;
            }

            RemoveCustomer request = new RemoveCustomer();

            if (context.bind(request)) {
                if (CustomerProvider.deleteCustomer(request.getId())) {
                    context.reply(new Response(Status.SUCCESS, "Customer deleted successfully"));
                } else {
                    context.reply(new Response(Status.FAILED, "Failed to delete customer"));
                }
            } else {
                context.reply(new Response(Status.FAILED, "Failed to delete customer"));
            }
        };
    }
}
