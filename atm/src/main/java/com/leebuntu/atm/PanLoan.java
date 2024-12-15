package com.leebuntu.atm;

import javax.swing.*;

import com.leebuntu.banking.BankingResult;
import com.leebuntu.banking.BankingResult.BankingResultType;
import com.leebuntu.banking.account.Account;

import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanLoan extends JPanel implements ActionListener, Pan {

    private JLabel Label_LoanGrade;
    private JLabel Label_Account;
    private JLabel Label_LoanAmount;
    private JTextField Text_LoanAmount;
    private JButton Btn_GetLoan;
    private JButton Btn_RequestReview;
    private JComboBox<String> Combo_Accounts;
    private JButton Btn_Close;
    private List<Account> accounts;

    private ATMMain MainFrame;

    public PanLoan(ATMMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Label_LoanGrade = new JLabel("대출 등급: 확인되지 않음");
        Label_LoanGrade.setBounds(20, 20, 400, 30);
        Label_LoanGrade.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        add(Label_LoanGrade);

        Label_Account = new JLabel("대출 금액을 받을 계좌:");
        Label_Account.setBounds(20, 70, 200, 30);
        Label_Account.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        add(Label_Account);

        Combo_Accounts = new JComboBox<>(new String[] { "계좌1", "계좌2", "계좌3", "계좌4", "계좌5" });
        Combo_Accounts.setBounds(200, 70, 250, 30);
        Combo_Accounts.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        add(Combo_Accounts);

        Label_LoanAmount = new JLabel("대출 금액:");
        Label_LoanAmount.setBounds(20, 120, 200, 30);
        Label_LoanAmount.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        add(Label_LoanAmount);

        Text_LoanAmount = new JTextField();
        Text_LoanAmount.setBounds(200, 120, 250, 30);
        Text_LoanAmount.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        add(Text_LoanAmount);

        Btn_RequestReview = new JButton("대출 심사받기");
        Btn_RequestReview.setBounds(70, 180, 150, 30);
        Btn_RequestReview.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        Btn_RequestReview.addActionListener(this);
        add(Btn_RequestReview);

        Btn_GetLoan = new JButton("대출 받기");
        Btn_GetLoan.setBounds(250, 180, 150, 30);
        Btn_GetLoan.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        Btn_GetLoan.addActionListener(this);
        add(Btn_GetLoan);

        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(180, 250, 100, 20);
        Btn_Close.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        Btn_Close.addActionListener(this);
        add(Btn_Close);
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        } else if (e.getSource() == Btn_RequestReview) {
            JOptionPane.showMessageDialog(this, "대출 심사를 요청했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == Btn_GetLoan) {
            String selectedAccount = (String) Combo_Accounts.getSelectedItem();
            String loanAmount = Text_LoanAmount.getText();
            if (loanAmount.isEmpty()) {
                JOptionPane.showMessageDialog(this, "대출 금액을 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, selectedAccount + " 계좌에 " + loanAmount + "원을 대출받았습니다.", "대출 성공",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
