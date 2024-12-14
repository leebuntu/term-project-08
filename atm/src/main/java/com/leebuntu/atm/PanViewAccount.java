package com.leebuntu.atm;

import javax.swing.*;

import com.leebuntu.common.banking.BankingResult;
import com.leebuntu.common.banking.BankingResult.BankingResultType;
import com.leebuntu.common.banking.account.Account;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PanViewAccount extends JPanel implements ActionListener {
    private JLabel Label_Account;
    private JLabel Label_balance;
    private JTextField Text_balance; // JTextField로 변경
    private JComboBox<String> Combo_Accounts;
    private List<Account> accounts;

    private JButton Btn_Close;

    ATMMain MainFrame;

    public PanViewAccount(ATMMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Combo_Accounts = new JComboBox<>();
        Combo_Accounts.setBounds(100, 70, 350, 20);
        Combo_Accounts.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        Combo_Accounts.addActionListener(this);
        Combo_Accounts.setSelectedIndex(-1);
        add(Combo_Accounts);

        Label_Account = new JLabel("계좌 조회");
        Label_Account.setBounds(0, 70, 100, 20);
        Label_Account.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Account);

        Label_balance = new JLabel("잔액");
        Label_balance.setBounds(0, 120, 100, 20);
        Label_balance.setHorizontalAlignment(JLabel.CENTER);
        add(Label_balance);

        Text_balance = new JTextField();
        Text_balance.setBounds(100, 120, 350, 20);
        Text_balance.setEditable(false);
        add(Text_balance);

        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(200, 250, 70, 20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    public void updateAccounts() {
        Combo_Accounts.removeAllItems();
        BankingResult result = BankConnector.getAccounts(MainFrame.token);
        if (result.getType() != BankingResultType.SUCCESS) {
            MainFrame.reset();
            return;
        }

        accounts = (List<Account>) result.getData();
        for (Account account : accounts) {
            Combo_Accounts.addItem(account.getAccountNumber());
        }

        GetBalance(0);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        } else if (e.getSource() == Combo_Accounts) {
            if (Combo_Accounts.getSelectedIndex() == -1) {
                return;
            }
            GetBalance(Combo_Accounts.getSelectedIndex());
        }
    }

    public void GetBalance(int index) {
        if (accounts == null || accounts.isEmpty()) {
            return;
        }

        Long balance = accounts.get(index).getTotalBalance();

        SwingUtilities.invokeLater(() -> {
            Text_balance.setText(BankUtils.displayBalance(balance) + "원");
        });
    }
}
