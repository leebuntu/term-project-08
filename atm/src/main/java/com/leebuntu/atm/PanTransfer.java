package com.leebuntu.atm;

import javax.swing.*;

import com.leebuntu.common.banking.BankingResult;
import com.leebuntu.common.banking.BankingResult.BankingResultType;
import com.leebuntu.common.banking.account.Account;
import com.leebuntu.common.banking.util.BankUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PanTransfer extends JPanel implements ActionListener, Pan {
    private JLabel Label_RecvAccount;
    private JTextField Text_RecvAccount;

    private JLabel Label_Balance;
    private JTextField Text_Balance;

    private JLabel Label_Amount;
    private JTextField Text_Amount;

    private JLabel Label_Title;

    private JButton Btn_Transfer;
    private JButton Btn_Close;
    private JLabel Label_Account;
    private JComboBox<String> Combo_Accounts;
    private List<Account> accounts;
    ATMMain MainFrame;

    public PanTransfer(ATMMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Label_Title = new JLabel("이체");
        Label_Title.setBounds(0, 20, 480, 40);
        Label_Title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        Label_Title.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Title);

        Combo_Accounts = new JComboBox<>();
        Combo_Accounts.setBounds(100, 90, 350, 20);
        Combo_Accounts.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        Combo_Accounts.addActionListener(this);
        add(Combo_Accounts);

        Label_Account = new JLabel("계좌 선택");
        Label_Account.setBounds(0, 90, 100, 20);
        Label_Account.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Account);

        Label_Balance = new JLabel("잔액");
        Label_Balance.setBounds(0, 120, 100, 20);
        Label_Balance.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Balance);

        Text_Balance = new JTextField();
        Text_Balance.setBounds(100, 120, 350, 20);
        Text_Balance.setEditable(false);
        add(Text_Balance);

        Label_RecvAccount = new JLabel("받는 계좌번호");
        Label_RecvAccount.setBounds(0, 150, 100, 20);
        Label_RecvAccount.setHorizontalAlignment(JLabel.CENTER);
        add(Label_RecvAccount);

        Text_RecvAccount = new JTextField();
        Text_RecvAccount.setBounds(100, 150, 350, 20);
        Text_RecvAccount.setEditable(true);
        Text_RecvAccount.setToolTipText("숫자만 입력");
        add(Text_RecvAccount);

        Label_Amount = new JLabel("이체금액");
        Label_Amount.setBounds(0, 180, 100, 20);
        Label_Amount.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Amount);

        Text_Amount = new JTextField();
        Text_Amount.setBounds(100, 180, 350, 20);
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
            updateBalance(Combo_Accounts.getSelectedIndex());
        }
    }

    @Override
    public void resetPanel() {
        Text_Amount.setText("");
        Text_RecvAccount.setText("");
        Combo_Accounts.removeAllItems();
        BankingResult result = BankConnector.getAccounts(MainFrame.token);
        if (result.getType() != BankingResultType.SUCCESS) {
            MainFrame.reset();
            return;
        }

        accounts = (List<Account>) result.getData();
        for (Account account : accounts) {
            Combo_Accounts.addItem(BankUtils.displayAccountNo(account.getAccountNumber()) + ", "
                    + account.getAccountType().getDescription());
        }

        updateBalance(0);
    }

    @Override
    public void backToMain() {
        this.setVisible(false);
        MainFrame.display("Main");
    }

    public void updateBalance(int index) {
        if (accounts == null || accounts.isEmpty()) {
            return;
        }

        long balance = accounts.get(index).getTotalBalance();
        Text_Balance.setText(BankUtils.displayBalance(balance) + "원");
    }

    public void Transfer() {
        String accountNo = accounts.get(Combo_Accounts.getSelectedIndex()).getAccountNumber();
        String receiveAccountNo = Text_RecvAccount.getText();
        long amount = Long.parseLong(Text_Amount.getText());

        if (amount <= 0) {
            JOptionPane.showMessageDialog(null, "금액은 0보다 커야 합니다.", "ERROR", JOptionPane.ERROR_MESSAGE);
            backToMain();
            return;
        }

        BankingResult result = BankConnector.transfer(MainFrame.token, accountNo, receiveAccountNo, amount);
        if (result.getType() != BankingResultType.SUCCESS) {
            JOptionPane.showMessageDialog(null, result.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            backToMain();
            return;
        }

        JOptionPane.showMessageDialog(null, "이체 성공", "SUCCESS", JOptionPane.PLAIN_MESSAGE);
        backToMain();
    }
}
