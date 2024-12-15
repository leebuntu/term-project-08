package com.leebuntu.atm;

import javax.swing.*;

import com.leebuntu.banking.BankingResult;
import com.leebuntu.banking.BankingResult.BankingResultType;
import com.leebuntu.banking.account.Account;
import com.leebuntu.banking.util.BankUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PanTransfer extends JPanel implements ActionListener, Pan {
    private JLabel Label_RecvAccount;
    private JTextField Text_RecvAccount;

    private JLabel Label_Amount;
    private JTextField Text_Amount;

    private JLabel Label_TotalBalance;
    private JTextField Text_TotalBalance;

    private JButton Btn_Transfer;
    private JButton Btn_Close;
    private JLabel Label_Account;
    private JComboBox<String> Combo_Accounts;
    ATMMain MainFrame;

    private List<Account> accounts;

    public PanTransfer(ATMMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Combo_Accounts = new JComboBox<>(new String[] { "계좌1", "계좌2", "계좌3", "계좌4", "계좌5" });
        Combo_Accounts.setBounds(100, 70, 350, 20);
        Combo_Accounts.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        Combo_Accounts.addActionListener(this);
        Combo_Accounts.setSelectedIndex(-1);
        add(Combo_Accounts);

        Label_Account = new JLabel("계좌 선택");
        Label_Account.setBounds(0, 70, 100, 20);
        Label_Account.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Account);

        Label_RecvAccount = new JLabel("받는 계좌번호 입력");
        Label_RecvAccount.setBounds(0, 100, 100, 20);
        Label_RecvAccount.setHorizontalAlignment(JLabel.CENTER);
        add(Label_RecvAccount);

        Text_RecvAccount = new JTextField();
        Text_RecvAccount.setBounds(100, 100, 350, 20);
        Text_RecvAccount.setEditable(true);
        Text_RecvAccount.setToolTipText("숫자만 입력");
        add(Text_RecvAccount);

        Label_TotalBalance = new JLabel("잔액");
        Label_TotalBalance.setBounds(0, 130, 100, 20);
        Label_TotalBalance.setHorizontalAlignment(JLabel.CENTER);
        add(Label_TotalBalance);

        Text_TotalBalance = new JTextField();
        Text_TotalBalance.setBounds(100, 130, 350, 20);
        Text_TotalBalance.setEditable(false);
        add(Text_TotalBalance);

        Label_Amount = new JLabel("이체금액");
        Label_Amount.setBounds(0, 160, 100, 20);
        Label_Amount.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Amount);

        Text_Amount = new JTextField();
        Text_Amount.setBounds(100, 160, 350, 20);
        Text_Amount.setEditable(true);
        Text_Amount.setToolTipText("숫자만 입력");
        add(Text_Amount);

        Btn_Transfer = new JButton("이체");
        Btn_Transfer.setBounds(150, 250, 70, 20);
        Btn_Transfer.addActionListener(this);
        add(Btn_Transfer);

        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(250, 250, 70, 20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Transfer) {
            Transfer();
            this.setVisible(false);
            MainFrame.display("Main");
        }
        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        }
        if (e.getSource() == Combo_Accounts) {
            if (Combo_Accounts.getSelectedIndex() == -1) {
                return;
            }
            updateTotalBalance(Combo_Accounts.getSelectedIndex());
        }
    }

    @Override
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

        updateTotalBalance(0);
    }

    public void updateTotalBalance(int index) {
        if (accounts == null || accounts.isEmpty()) {
            return;
        }

        Long balance = accounts.get(index).getTotalBalance();

        SwingUtilities.invokeLater(() -> {
            Text_TotalBalance.setText(BankUtils.displayBalance(balance) + "원");
        });
    }

    public void Transfer() {
        String accountNo = Combo_Accounts.getSelectedItem().toString();
        String receiveAccountNo = Text_RecvAccount.getText();
        long amount = Long.parseLong(Text_Amount.getText());

        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "금액은 0보다 커야 합니다.", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BankingResult result = BankConnector.transfer(MainFrame.token, accountNo, receiveAccountNo, amount);
        if (result.getType() != BankingResultType.SUCCESS) {
            JOptionPane.showMessageDialog(null, result.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, "이체 성공", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
    }
}
