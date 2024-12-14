package com.leebuntu;

import javax.swing.*;

import com.leebuntu.banking.BankingResult;
import com.leebuntu.banking.BankingResult.BankingResultType;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

//*******************************************************************
// Name : PanWithdrawal
// Type : Class
// Description :  출금 화면 패널을 구현한 Class 이다.
//*******************************************************************
public class PanWithdrawal extends JPanel implements ActionListener {
    private JLabel Label_Title;
    private JLabel Label_Amount;
    private JTextField Text_Amount;
    private JButton Btn_Transfer;
    private JButton Btn_Close;
    private JComboBox<String> Combo_Accounts;
    private JLabel Label_Account;
    ATMMain MainFrame;

    // *******************************************************************
    // Name : PanWithdrawal()
    // Type : 생성자
    // Description : PanDeposite Class의 생성자 구현
    // *******************************************************************
    public PanWithdrawal(ATMMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Label_Title = new JLabel("출금");
        Label_Title.setBounds(0, 0, 480, 40);
        Label_Title.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Title);

        Combo_Accounts = new JComboBox<>();
        Combo_Accounts.setBounds(100, 70, 350, 20);
        Combo_Accounts.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        add(Combo_Accounts);

        Label_Account = new JLabel("계좌 선택");
        Label_Account.setBounds(0, 70, 100, 20);
        Label_Account.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Account);

        Label_Amount = new JLabel("금액");
        Label_Amount.setBounds(0, 120, 100, 20);
        Label_Amount.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Amount);

        Text_Amount = new JTextField();
        Text_Amount.setBounds(100, 120, 350, 20);
        Text_Amount.setEditable(true);
        Text_Amount.setToolTipText("숫자만 입력");
        add(Text_Amount);

        Btn_Transfer = new JButton("출금");
        Btn_Transfer.setBounds(150, 250, 70, 20);
        Btn_Transfer.addActionListener(this);
        add(Btn_Transfer);

        Btn_Close = new JButton("취소");
        Btn_Close.setBounds(250, 250, 70, 20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    // *******************************************************************
    // Name : actionPerformed
    // Type : Listner
    // Description : 입금 버튼, 취소 버튼의 동작을 구현
    // 입금, 취소 동작 후 메인 화면으로 변경되도록 구현
    // *******************************************************************
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Transfer) {
            Withdrawal();
            this.setVisible(false);
            MainFrame.display("Main");
        }
        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        }
    }

    public void updateAccounts() {
        Combo_Accounts.removeAllItems();
        BankingResult result = BankConnector.getFormattedAccounts(MainFrame.token);
        if (result.getType() != BankingResultType.SUCCESS) {
            MainFrame.reset();
            return;
        }

        for (String account : (List<String>) result.getData()) {
            Combo_Accounts.addItem(account);
        }
    }

    // *******************************************************************
    // Name : Withdrawal()
    // Type : Method
    // Description : 출금 화면의 데이터를 가지고 있는 CommandDTO를 생성하고,
    // ATMMain의 Send 기능을 호출하여 서버에 출금 요청 메시지를 전달 하는 기능.
    // *******************************************************************
    public void Withdrawal() {
        try {
            long amount = Long.parseLong(Text_Amount.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(null, "금액은 0보다 커야 합니다.", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String accountNumber = Combo_Accounts.getSelectedItem().toString();

            BankingResult result = BankConnector.withdraw(MainFrame.token, accountNumber, amount);
            if (result.getType() != BankingResultType.SUCCESS) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, result.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                });
                return;
            }

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "출금 성공", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            });
        } catch (NumberFormatException ex) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "유효한 금액을 입력하세요.", "ERROR", JOptionPane.ERROR_MESSAGE);
            });
        }
    }
}
