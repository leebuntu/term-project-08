package com.leebuntu.atm;

import javax.swing.*;

import com.leebuntu.common.banking.BankingResult;
import com.leebuntu.common.banking.BankingResult.BankingResultType;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PanDeposite extends JPanel implements ActionListener {
    private JLabel Label_Title;
    private JLabel Label_Amount;
    private JTextField Text_Amount;
    private JButton Btn_Deposite;
    private JButton Btn_Close;
    private JLabel Label_Account;
    private JComboBox<String> Combo_Accounts;
    ATMMain MainFrame;

    public PanDeposite(ATMMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Label_Title = new JLabel("입금");
        Label_Title.setBounds(0, 0, 480, 40);
        Label_Title.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Title);

        Combo_Accounts = new JComboBox<>();
        Combo_Accounts.setBounds(100, 70, 350, 20);
        Combo_Accounts.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        add(Combo_Accounts);

        Label_Account = new JLabel("계좌 선택");
        Label_Account.setBounds(10, 70, 100, 20);
        Label_Account.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Account);

        Label_Amount = new JLabel("금액");
        Label_Amount.setBounds(10, 120, 100, 20);
        Label_Amount.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Amount);

        Text_Amount = new JTextField();
        Text_Amount.setBounds(100, 120, 350, 20);
        Text_Amount.setEditable(true);
        add(Text_Amount);

        Btn_Deposite = new JButton("입금");
        Btn_Deposite.setBounds(150, 250, 70, 20);
        Btn_Deposite.addActionListener(this);
        add(Btn_Deposite);

        Btn_Close = new JButton("취소");
        Btn_Close.setBounds(250, 250, 70, 20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    public void updateAccounts() {
        Combo_Accounts.removeAllItems();
        BankingResult result = BankConnector.getFormattedAccounts(MainFrame.token);
        if (result.getType() != BankingResultType.SUCCESS) {
            MainFrame.reset();
            return;
        }

        for (String accountNumber : (List<String>) result.getData()) {
            Combo_Accounts.addItem(accountNumber);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Deposite) {
            deposit();
        }

        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        }
    }

    public void deposit() {
        try {
            long amount = Long.parseLong(Text_Amount.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "금액은 0보다 커야 합니다.", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String accountNumber = (String) Combo_Accounts.getSelectedItem();

            BankingResult result = BankConnector.deposit(MainFrame.token, accountNumber, amount);
            if (result.getType() != BankingResultType.SUCCESS) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, result.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                });
                return;
            }

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "입금 성공", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            });

        } catch (NumberFormatException ex) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "입금 금액을 정확히 입력해주세요.", "ERROR", JOptionPane.ERROR_MESSAGE);
            });
        }
    }
}
