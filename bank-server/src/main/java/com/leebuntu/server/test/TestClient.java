package com.leebuntu.server.test;// package com.leebuntu.server.test;

// import com.leebuntu.server.banking.account.Account;
// import com.leebuntu.server.banking.account.CheckingAccount;
// import com.leebuntu.server.banking.account.SavingsAccount;
// import com.leebuntu.server.banking.customer.Customer;
// import com.leebuntu.server.communication.Connector;
// import com.leebuntu.server.communication.dto.GeneralResponse;
// import com.leebuntu.server.communication.dto.Payload;
// import
// com.leebuntu.server.communication.dto.admin.request.banking.CreateCheckingAccount;
// import
// com.leebuntu.server.communication.dto.admin.request.banking.CreateSavingsAccount;
// import
// com.leebuntu.server.communication.dto.admin.request.customer.CreateCustomer;
// import com.leebuntu.server.communication.dto.admin.response.banking.Accounts;
// import
// com.leebuntu.server.communication.dto.admin.response.customer.Customers;
// import com.leebuntu.server.communication.dto.user.request.customer.Login;
// import com.leebuntu.server.communication.packet.Packet;

// public class TestClient {
// public static void main(String[] args) throws Exception {
// Connector connector = new Connector("localhost", 8080);

// Login login = new Login("admin", "admin123");

// Packet<Login> packet = new Packet<>();
// packet.setPath("/login");
// packet.setPayload(login);

// connector.send(packet);

// GeneralResponse response = new GeneralResponse();
// connector.receiveAndBind(response);

// System.out.println(response.getStatus());
// System.out.println(response.getMessage());

// Packet<Payload> packet2 = new Packet<>();
// packet2.setPath("/admin/accounts/get/all");
// packet2.setAuthToken(response.getMessage());

// connector.send(packet2);

// GeneralResponse response2 = new GeneralResponse();
// connector.receiveAndBind(response2);

// System.out.println(response2.getStatus());
// System.out.println(response2.getMessage());
// }

// }
