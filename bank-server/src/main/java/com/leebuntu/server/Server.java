package com.leebuntu.server;

import javax.swing.*;

import com.leebuntu.common.banking.customer.CustomerType;
import com.leebuntu.server.communication.jwt.JWTMiddleware;
import com.leebuntu.server.communication.router.Router;
import com.leebuntu.server.db.core.Database;
import com.leebuntu.server.db.core.DatabaseManager;
import com.leebuntu.server.handler.admin.banking.AccountHandler;
import com.leebuntu.server.handler.admin.customer.CustomerHandler;
import com.leebuntu.server.handler.user.banking.TransactionHandler;
import com.leebuntu.server.handler.user.customer.LoginHandler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.Instant;

public class Server extends JFrame implements ActionListener {
    public enum LogType {
        INFO,
        ERROR,
        WARNING,
        DEBUG
    }

    private JLabel Label_UserCount;
    private JLabel Label_UserCount_2;
    private JToggleButton Btn_StartStop;
    private JButton Btn_Reset;
    private JTextArea TextArea_Log;
    private JScrollPane sp;

    private Router router;

    private Thread userCountThread;

    private static Server instance = null;

    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public Server() {
        InitGui();
        setVisible(true);
    }

    private void InitGui() {
        setTitle("서버 GUI");
        setSize(480, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        Label_UserCount = new JLabel("현재 유저 수: ");
        topPanel.add(Label_UserCount);

        Label_UserCount_2 = new JLabel("0");
        topPanel.add(Label_UserCount_2);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        TextArea_Log = new JTextArea();
        TextArea_Log.setEditable(false);
        sp = new JScrollPane(TextArea_Log);
        mainPanel.add(sp, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        Btn_StartStop = new JToggleButton("시작");
        Btn_StartStop.addActionListener(this);
        bottomPanel.add(Btn_StartStop);

        Btn_Reset = new JButton("텍스트 창 초기화");
        Btn_Reset.addActionListener(this);
        bottomPanel.add(Btn_Reset);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static void buildDB() {
        boolean isNew = true;
        try {
            isNew = DBBuild.buildUsersDB();
            DBBuild.buildAccountsDB();
            DBBuild.buildTransactionsDB();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // DB를 초기화
        DatabaseManager.initDB(Constants.DB_ROOT_PATH, Constants.CUTOMER_DB_NAME);
        DatabaseManager.initDB(Constants.DB_ROOT_PATH, Constants.ACCOUNT_DB_NAME);
        DatabaseManager.initDB(Constants.DB_ROOT_PATH, Constants.TRANSACTION_DB_NAME);

        if (isNew) {
            registerAdmin();
        }
    }

    private static void registerAdmin() {
        String query = "INSERT INTO user customer_type, customer_id, password";
        Database db = DatabaseManager.getDB(Constants.CUTOMER_DB_NAME);
        db.execute(query, CustomerType.ADMIN.ordinal(), "admin", "admin123");
    }

    private void startServer() {
        try {
            buildDB();

            router = new Router(8080);

            router.addRoute("/admin/customers/get", CustomerHandler.getCustomers(), JWTMiddleware.getJWTMiddleware());
            router.addRoute("/admin/customers/create", CustomerHandler.createCustomer(),
                    JWTMiddleware.getJWTMiddleware());
            router.addRoute("/admin/customers/delete", CustomerHandler.deleteCustomer(),
                    JWTMiddleware.getJWTMiddleware());
            router.addRoute("/admin/customers/update", CustomerHandler.updateCustomer(),
                    JWTMiddleware.getJWTMiddleware());

            router.addRoute("/admin/accounts/create", AccountHandler.createAccount(), JWTMiddleware.getJWTMiddleware());
            router.addRoute("/admin/accounts/get", AccountHandler.getAccount(), JWTMiddleware.getJWTMiddleware());
            router.addRoute("/admin/accounts/get/all", AccountHandler.getAllAccounts(),
                    JWTMiddleware.getJWTMiddleware());
            router.addRoute("/admin/accounts/delete", AccountHandler.deleteAccount(), JWTMiddleware.getJWTMiddleware());
            router.addRoute("/admin/accounts/update", AccountHandler.updateAccount(), JWTMiddleware.getJWTMiddleware());

            router.addRoute("/login", LoginHandler.getLoginHandler());

            router.addRoute("/banking/account/withdraw",
                    com.leebuntu.server.handler.user.banking.AccountHandler.withdraw(),
                    JWTMiddleware.getJWTMiddleware());
            router.addRoute("/banking/account/deposit",
                    com.leebuntu.server.handler.user.banking.AccountHandler.deposit(),
                    JWTMiddleware.getJWTMiddleware());
            router.addRoute("/banking/account/transfer",
                    com.leebuntu.server.handler.user.banking.AccountHandler.transfer(),
                    JWTMiddleware.getJWTMiddleware());
            router.addRoute("/banking/account/get",
                    com.leebuntu.server.handler.user.banking.AccountHandler.getAccounts(),
                    JWTMiddleware.getJWTMiddleware());

            router.addRoute("/banking/transaction/get",
                    TransactionHandler.getTransactions(),
                    JWTMiddleware.getJWTMiddleware());

            router.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopServer() {
        if (router != null) {
            router.stop();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_StartStop) {
            if (Btn_StartStop.isSelected()) {
                startServer();
                userCountThread = new Thread(this::setUserCount);
                userCountThread.start();
                printToLog(LogType.INFO, "서버 시작");
            } else {
                userCountThread.interrupt();
                stopServer();
                printToLog(LogType.INFO, "서버 종료");
            }
        } else if (e.getSource() == Btn_Reset) {
            TextArea_Log.setText(null);
        }
    }

    private void setUserCount() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            SwingUtilities.invokeLater(() -> Label_UserCount_2.setText(String.valueOf(router.getActiveConnections())));
        }
    }

    public void printToLog(LogType type, String msg) {
        addMsg(type, msg);
    }

    private void addMsg(LogType type, String data) {
        TextArea_Log
                .append("[" + type.toString().toUpperCase() + "] [" + Instant.now().toString() + "] " + data + "\n");
    }

    public static void main(String[] args) throws Exception {
        Server.getInstance();
    }
}
