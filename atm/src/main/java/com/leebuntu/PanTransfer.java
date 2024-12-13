package com.leebuntu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

public class PanTransfer extends JPanel implements ActionListener {
    private JLabel Label_RecvAccount;
    private JTextField Text_RecvAccount;

    private JLabel Label_Amount;
    private JTextField Text_Amount;

    private JLabel Label_Password;
    private JTextField Text_Password;

    private JButton Btn_Transfer;
    private JButton Btn_Close;
    private JLabel Label_Account;
    private JComboBox<String> Combo_Accounts;
    ATMMain MainFrame;

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
        add(Combo_Accounts);

        Label_Account = new JLabel("계좌 선택");
        Label_Account.setBounds(0, 70, 100, 20);
        Label_Account.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Account);

        Label_Account = new JLabel("계좌번호 입력");
        Label_Account.setBounds(0, 120, 100, 20);
        Label_Account.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Account);

        Text_Amount = new JTextField();
        Text_Amount.setBounds(100, 120, 350, 20);
        Text_Amount.setEditable(true);
        Text_Amount.setToolTipText("숫자만 입력");
        add(Text_Amount);

        Label_Amount = new JLabel("이체금액");
        Label_Amount.setBounds(0, 170, 100, 20);
        Label_Amount.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Amount);

        Text_Amount = new JTextField();
        Text_Amount.setBounds(100, 170, 350, 20);
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
    }

    public void Transfer() {
        String receiveAccountNo = Text_RecvAccount.getText();
        long amount = Long.parseLong(Text_Amount.getText());

        CommandDTO commandDTO = new CommandDTO(RequestType.TRANSFER, password, MainFrame.userId, receiveAccountNo,
                amount);
        MainFrame.send(commandDTO, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    return;
                }
                attachment.flip();
                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(attachment.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    CommandDTO command = (CommandDTO) objectInputStream.readObject();
                    SwingUtilities.invokeLater(() -> {
                        String contentText = null;
                        if (command.getResponseType() == ResponseType.INSUFFICIENT) {
                            contentText = "잔액이 부족합니다.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (command.getResponseType() == ResponseType.WRONG_ACCOUNT_NO) {
                            contentText = "계좌번호가 존재하지 않습니다.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE",
                                    JOptionPane.ERROR_MESSAGE);
                        } else if (command.getResponseType() == ResponseType.WRONG_PASSWORD) {
                            contentText = "비밀번호가 일치하지 않습니다.";
                            JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            contentText = "이체 되었습니다.";
                            JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE",
                                    JOptionPane.PLAIN_MESSAGE);
                        }
                    });
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });
    }
}
