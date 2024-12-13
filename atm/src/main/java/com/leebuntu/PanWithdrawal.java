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

        Combo_Accounts = new JComboBox<>(new String[] { "계좌1", "계좌2", "계좌3", "계좌4", "계좌5" });
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

    // *******************************************************************
    // Name : Withdrawal()
    // Type : Method
    // Description : 출금 화면의 데이터를 가지고 있는 CommandDTO를 생성하고,
    // ATMMain의 Send 기능을 호출하여 서버에 출금 요청 메시지를 전달 하는 기능.
    // *******************************************************************
    public void Withdrawal() {
        try {

            long amount = Long.parseLong(Text_Amount.getText());

            CommandDTO commandDTO = new CommandDTO(RequestType.WITHDRAW, MainFrame.userId, amount);

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
                            String contentText;
                            if (command.getResponseType() == ResponseType.SUCCESS) {
                                contentText = "출금 되었습니다.";
                                JOptionPane.showMessageDialog(null, contentText, "SUCCESS_MESSAGE",
                                        JOptionPane.PLAIN_MESSAGE);
                            } else if (command.getResponseType() == ResponseType.INSUFFICIENT) {
                                contentText = "잔액이 부족합니다";
                                JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                contentText = "출금 실패";
                                JOptionPane.showMessageDialog(null, contentText, "ERROR_MESSAGE",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {

                }
            });
        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(this, "유효한 금액을 입력하세요.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
