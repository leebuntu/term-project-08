// package com.leebuntu;

// import com.leebuntu.communication.router.Router;
// import com.leebuntu.handler.admin.banking.AccountHandler;
// import com.leebuntu.handler.admin.customer.CustomerHandler;
// import com.leebuntu.handler.user.customer.LoginHandler;

// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.io.IOException;
// import java.sql.Date;
// import java.time.LocalDate;

// class Server extends JFrame implements ActionListener {
// private JLabel Label_UserCount;
// private JLabel Label_UserCount_2;
// private JToggleButton Btn_StartStop;
// private JButton Btn_Reset;
// private JTextArea TextArea_Log;
// private JScrollPane sp;

// private Router router;

// private Thread userCountThread;

// private boolean isRunning;

// public Server() {
// InitGui();
// setVisible(true);

// }

// private void InitGui() {
// setTitle("서버 GUI");
// setSize(480, 320);
// setDefaultCloseOperation(EXIT_ON_CLOSE);
// setResizable(false);

// JPanel mainPanel = new JPanel();
// mainPanel.setLayout(new BorderLayout());

// JPanel topPanel = new JPanel();
// topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

// Label_UserCount = new JLabel("현재 유저 수: ");
// topPanel.add(Label_UserCount);

// Label_UserCount_2 = new JLabel("0");
// topPanel.add(Label_UserCount_2);

// mainPanel.add(topPanel, BorderLayout.NORTH);

// TextArea_Log = new JTextArea();
// TextArea_Log.setEditable(false);
// sp = new JScrollPane(TextArea_Log);
// mainPanel.add(sp, BorderLayout.CENTER);

// JPanel bottomPanel = new JPanel();
// bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

// Btn_StartStop = new JToggleButton("시작");
// Btn_StartStop.addActionListener(this);
// bottomPanel.add(Btn_StartStop);

// Btn_Reset = new JButton("텍스트 창 초기화");
// Btn_Reset.addActionListener(this);
// bottomPanel.add(Btn_Reset);

// mainPanel.add(bottomPanel, BorderLayout.SOUTH);

// add(mainPanel);
// setLocationRelativeTo(null);
// setVisible(true);
// }

// private void startServer() {
// try {
// router = new Router(8080);

// router.addRoute("/admin/customers/get", CustomerHandler.getCustomers());
// router.addRoute("/admin/customers/create", CustomerHandler.createCustomer());
// router.addRoute("/admin/customers/delete", CustomerHandler.deleteCustomer());

// router.addRoute("/admin/accounts/create", AccountHandler.createAccount());
// router.addRoute("/admin/accounts/get", AccountHandler.getAccount());
// router.addRoute("/admin/accounts/get/all", AccountHandler.getAllAccounts());

// router.addRoute("/login", LoginHandler.getLoginHandler());

// router.start();
// } catch (IOException e) {
// e.printStackTrace();
// }
// }

// private void stopServer() {
// if (router != null) {
// router.stop();
// }
// }

// public void actionPerformed(ActionEvent e) {
// if (e.getSource() == Btn_StartStop) {
// if (Btn_StartStop.isSelected()) {
// startServer();
// userCountThread = new Thread(this::setUserCount);
// userCountThread.start();
// addMsg("서버 시작: " + Date.valueOf(LocalDate.now()));
// } else {
// userCountThread.interrupt();
// stopServer();
// addMsg("서버 종료: " + Date.valueOf(LocalDate.now()));
// }
// } else if (e.getSource() == Btn_Reset) {
// TextArea_Log.setText(null);
// }
// }

// private void setUserCount() {
// while (true) {
// try {
// Thread.sleep(1000);
// } catch (InterruptedException e) {
// e.printStackTrace();
// }
// System.out.println(router.getActiveConnections());
// SwingUtilities.invokeLater(() ->
// Label_UserCount_2.setText(String.valueOf(router.getActiveConnections())));
// }
// }

// public void displayInfo(String msg) {
// addMsg(msg);
// }

// public void addMsg(String data) {
// TextArea_Log.append(data + "\n");
// }

// public static void main(String[] args) throws Exception {
// Server f = new Server();
// }
// }
