package com.leebuntu;

import javax.swing.*;

import com.leebuntu.communication.dto.request.banking.ViewAccount;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.List;

public class PanViewAccount extends JPanel implements ActionListener {
    private JLabel Label_Account;
    private JTextField Text_Account; // JTextField로 변경
    private JLabel Label_balance;
    private JTextField Text_balance; // JTextField로 변경
    private JComboBox<String> Combo_Accounts;

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
        add(Text_balance);

        Btn_Close = new JButton("닫기");
        Btn_Close.setBounds(200, 250, 70, 20);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    public void updateAccounts() {
        Combo_Accounts.removeAllItems();
        List<String> accounts = BankConnector.getFormattedAccounts(MainFrame.token);
        if (accounts == null) {
            return;
        }

        for (String account : accounts) {
            Combo_Accounts.addItem(account);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        }
    }

    public void GetBalance() {
        String accountNo = Combo_Accounts.getSelectedItem().toString();

        // MainFrame.send(new CommandDTO(RequestType.VIEW), new
        // CompletionHandler<Integer, ByteBuffer>() {
        // @Override
        // public void completed(Integer result, ByteBuffer attachment) {
        // if (result == -1) {
        // return;
        // }
        // attachment.flip();
        // try {
        // ByteArrayInputStream byteArrayInputStream = new
        // ByteArrayInputStream(attachment.array());
        // ObjectInputStream objectInputStream = new
        // ObjectInputStream(byteArrayInputStream);
        // CommandDTO command = (CommandDTO) objectInputStream.readObject();
        // SwingUtilities.invokeLater(() -> {
        // String accountNumber =
        // BankUtils.displayAccountNo(command.getAccountNumber());
        // Text_Account.setText(accountNumber);
        // String balance = BankUtils.displayBalance(command.getBalance());
        // Text_balance.setText(balance + "원");
        // });
        // } catch (IOException e) {
        // e.printStackTrace();
        // } catch (ClassNotFoundException e) {
        // e.printStackTrace();
        // }
        // }

        // @Override
        // public void failed(Throwable exc, ByteBuffer attachment) {
        // }
        // });
    }
}
