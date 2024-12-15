package com.leebuntu.atm;

import javax.swing.*;

import com.leebuntu.banking.BankingResult;
import com.leebuntu.banking.BankingResult.BankingResultType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanLogin extends JPanel implements ActionListener {
    private JLabel Label_Title;
    private JLabel Label_ID;
    private JTextField Text_ID;
    private JLabel Label_Password;
    private JPasswordField Text_Password;
    private JButton Btn_Login;
    private JButton Btn_Close;
    private ATMMain MainFrame;

    public PanLogin(ATMMain parent) {
        MainFrame = parent;
        InitGUI();
    }

    private void InitGUI() {
        setLayout(null);
        setBounds(0, 0, 480, 320);

        Label_Title = new JLabel("로그인");
        Label_Title.setBounds(0, 0, 480, 40);
        Label_Title.setHorizontalAlignment(JLabel.CENTER);
        add(Label_Title);

        Label_ID = new JLabel("아이디");
        Label_ID.setBounds(20, 80, 100, 20);
        add(Label_ID);

        Text_ID = new JTextField();
        Text_ID.setBounds(100, 80, 350, 20);
        add(Text_ID);

        Label_Password = new JLabel("비밀번호");
        Label_Password.setBounds(20, 120, 100, 20);
        add(Label_Password);

        Text_Password = new JPasswordField();
        Text_Password.setBounds(100, 120, 350, 20);
        Text_Password.setEchoChar('*');
        add(Text_Password);

        Btn_Login = new JButton("로그인");
        Btn_Login.setBounds(150, 200, 100, 30);
        Btn_Login.addActionListener(this);
        add(Btn_Login);

        Btn_Close = new JButton("취소");
        Btn_Close.setBounds(270, 200, 100, 30);
        Btn_Close.addActionListener(this);
        add(Btn_Close);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_Login) {
            Login();
        } else if (e.getSource() == Btn_Close) {
            this.setVisible(false);
            MainFrame.display("Main");
        }
    }

    public void Login() {
        String id = Text_ID.getText().trim();
        String password = new String(Text_Password.getPassword()).trim();

        if (id.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "아이디와 비밀번호를 모두 입력해주세요.", "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BankingResult result = BankConnector.login(id, password);
        if (result.getType() != BankingResultType.SUCCESS) {
            JOptionPane.showMessageDialog(null, result.getMessage(), "ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            String message;
            MainFrame.token = (String) result.getData();
            message = "로그인되었습니다.";
            setVisible(false);
            MainFrame.display("Main");
            JOptionPane.showMessageDialog(null, message);
        });
    }

}
