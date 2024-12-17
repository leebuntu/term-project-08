package com.leebuntu.atm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.leebuntu.common.banking.BankingResult;
import com.leebuntu.common.banking.BankingResult.BankingResultType;
import com.leebuntu.common.banking.Transaction;
import com.leebuntu.common.banking.account.Account;
import com.leebuntu.common.banking.util.BankUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanViewAccount extends JPanel implements ActionListener, Pan {
    private JLabel Label_Account;
    private JLabel Label_balance;
    private JLabel Label_Title;
    private JTextField Text_balance; // JTextField로 변경
    private JComboBox<String> Combo_Accounts;
    private List<Account> accounts;
    private List<Transaction> transactions;
    private JButton Btn_Close;
    private String[] columnNames = { "출금 계좌", "수신 계좌", "금액", "일시" };
    private JTable Table_Account;
    ATMMain MainFrame;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());

    public PanViewAccount(ATMMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Label_Title = new JLabel("계좌 조회");
        Label_Title.setBounds(0, 10, 480, 40);
        Label_Title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        Label_Title.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Title);

        Table_Account = new JTable(new Object[0][0], columnNames);
        JScrollPane scrollPane = new JScrollPane(Table_Account);
        scrollPane.setBounds(15, 130, 450, 100);
        add(scrollPane);

        Combo_Accounts = new JComboBox<>();
        Combo_Accounts.setBounds(100, 60, 350, 20);
        Combo_Accounts.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        Combo_Accounts.addActionListener(this);
        Combo_Accounts.setSelectedIndex(-1);
        add(Combo_Accounts);

        Label_Account = new JLabel("계좌 조회");
        Label_Account.setBounds(0, 60, 100, 20);
        Label_Account.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Account);

        Label_balance = new JLabel("잔액");
        Label_balance.setBounds(0, 90, 100, 20);
        Label_balance.setHorizontalAlignment(JLabel.CENTER);
        add(Label_balance);

        Text_balance = new JTextField();
        Text_balance.setBounds(100, 90, 350, 20);
        Text_balance.setEditable(false);
        add(Text_balance);

        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(200, 250, 70, 20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    @Override
    public void resetPanel() {
        Combo_Accounts.removeAllItems();
        BankingResult result = BankConnector.getAccounts(MainFrame.token);
        if (result.getType() != BankingResultType.SUCCESS) {
            MainFrame.reset();
            return;
        }

        accounts = (List<Account>) result.getData();
        for (Account account : accounts) {
            Combo_Accounts.addItem(account.getAccountNumber() + ", " + account.getAccountType().getDescription());
        }

        updateBalance(0);
    }

    @Override
    public void backToMain() {
        this.setVisible(false);
        MainFrame.display("Main");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        } else if (e.getSource() == Combo_Accounts) {
            if (Combo_Accounts.getSelectedIndex() == -1) {
                return;
            }
            updateBalance(Combo_Accounts.getSelectedIndex());
        }
    }

    private void adjustColumnWidths() {
        Table_Account.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        Table_Account.getColumnModel().getColumn(0).setPreferredWidth(100);
        Table_Account.getColumnModel().getColumn(1).setPreferredWidth(100);
        Table_Account.getColumnModel().getColumn(2).setPreferredWidth(105);
        Table_Account.getColumnModel().getColumn(3).setPreferredWidth(140);
    }

    public void updateBalance(int index) {
        if (accounts == null || accounts.isEmpty()) {
            return;
        }

        long balance = accounts.get(index).getTotalBalance();

        BankingResult result = BankConnector.getTransactions(MainFrame.token,
                accounts.get(index).getAccountNumber());
        if (result.getType() != BankingResultType.SUCCESS) {
            return;
        }

        transactions = (List<Transaction>) result.getData();

        SwingUtilities.invokeLater(() -> {
            Text_balance.setText(BankUtils.displayBalance(balance) + "원");
            Object[][] data = new Object[transactions.size()][4];
            for (int i = 0; i < transactions.size(); i++) {
                data[i][0] = transactions.get(i).getSenderAccountNumber();
                data[i][1] = transactions.get(i).getReceiverAccountNumber();
                data[i][2] = BankUtils.displayBalance(transactions.get(i).getAmount());
                data[i][3] = formatter.format(Instant.ofEpochMilli(transactions.get(i).getDate()));
            }
            Table_Account.setModel(new DefaultTableModel(data, columnNames));
            adjustColumnWidths();
        });
    }
}
