package com.leebuntu.atm;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ATMMain extends JFrame implements ActionListener {

    private JLabel Label_Title;
    private RoundedButton Btn_ViewAccount;
    private RoundedButton Btn_Transfer;
    private RoundedButton Btn_Login;
    private RoundedButton Btn_Deposite;
    private RoundedButton Btn_Withdrawal;
    private RoundedButton Btn_Loan;
    private RoundedButton Btn_EnlargeText;
    private JLabel Label_Image;

    PanViewAccount Pan_ViewAccount;
    PanTransfer Pan_Transfer;
    PanDeposite Pan_Deposite;
    PanWithdrawal Pan_Withdrawal;
    PanLogin Pan_Login;
    PanLoan Pan_Loan;

    public String token;

    public ATMMain() {
        InitGui();
        setVisible(true);
    }

    private void InitGui() {
        setTitle("ATM GUI");
        setBounds(0, 0, 480, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        BackgroundPanel backgroundPanel = new BackgroundPanel("/Users/buntu/Desktop/파란 배경.jpg");
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        Label_Title = new JLabel("CNU Bank ATM");
        Label_Title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        Label_Title.setSize(480, 55);
        Label_Title.setLocation(10, 0);
        Label_Title.setForeground(Color.WHITE);
        Label_Title.setHorizontalAlignment(JLabel.LEFT);
        backgroundPanel.add(Label_Title);

        try {
            Image Img_CNULogo = ImageIO.read(new File("/Users/buntu/Desktop/cnu.jpg"));
            ImageIcon IconCNU = new ImageIcon(Img_CNULogo.getScaledInstance(200, 130, Image.SCALE_SMOOTH));
            Label_Image = new JLabel(IconCNU);
            Label_Image.setBounds(135, 60, IconCNU.getIconWidth(), IconCNU.getIconHeight());
            backgroundPanel.add(Label_Image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Btn_ViewAccount = new RoundedButton("계좌 조회");
        Btn_ViewAccount.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        Btn_ViewAccount.setSize(115, 60);
        Btn_ViewAccount.setLocation(10, 60);
        Btn_ViewAccount.addActionListener(this);
        Btn_ViewAccount.setBackground(Color.WHITE);
        backgroundPanel.add(Btn_ViewAccount);

        Btn_Transfer = new RoundedButton("계좌 이체");
        Btn_Transfer.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        Btn_Transfer.setSize(115, 60);
        Btn_Transfer.setLocation(10, 130);
        Btn_Transfer.addActionListener(this);
        Btn_Transfer.setBackground(Color.WHITE);
        backgroundPanel.add(Btn_Transfer);

        Btn_Login = new RoundedButton("로그인");
        Btn_Login.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        Btn_Login.setSize(115, 60);
        Btn_Login.setLocation(10, 200);
        Btn_Login.addActionListener(this);
        Btn_Login.setBackground(Color.WHITE);
        backgroundPanel.add(Btn_Login);

        Btn_Deposite = new RoundedButton("입금");
        Btn_Deposite.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        Btn_Deposite.setSize(115, 60);
        Btn_Deposite.setLocation(345, 60);
        Btn_Deposite.setBackground(Color.WHITE);
        Btn_Deposite.addActionListener(this);
        backgroundPanel.add(Btn_Deposite);

        Btn_Withdrawal = new RoundedButton("출금");
        Btn_Withdrawal.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        Btn_Withdrawal.setSize(115, 60);
        Btn_Withdrawal.setLocation(345, 130);
        Btn_Withdrawal.setBackground(Color.WHITE);
        Btn_Withdrawal.addActionListener(this);
        backgroundPanel.add(Btn_Withdrawal);

        Btn_Loan = new RoundedButton("대출");
        Btn_Loan.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        Btn_Loan.setSize(115, 60);
        Btn_Loan.setLocation(345, 200);
        Btn_Loan.addActionListener(this);
        Btn_Loan.setBackground(Color.WHITE);
        backgroundPanel.add(Btn_Loan);

        Btn_EnlargeText = new RoundedButton("글자 확대");
        Btn_EnlargeText.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        Btn_EnlargeText.setSize(200, 60);
        Btn_EnlargeText.setLocation(135, 200);
        Btn_EnlargeText.setBackgroundColor(Color.BLUE);
        Btn_EnlargeText.setTextColor(Color.WHITE);
        Btn_EnlargeText.addActionListener(this);
        backgroundPanel.add(Btn_EnlargeText);

        Pan_ViewAccount = new PanViewAccount(this);
        add(Pan_ViewAccount);
        Pan_ViewAccount.setVisible(false);

        Pan_Transfer = new PanTransfer(this);
        add(Pan_Transfer);
        Pan_Transfer.setVisible(false);

        Pan_Deposite = new PanDeposite(this);
        add(Pan_Deposite);
        Pan_Deposite.setVisible(false);

        Pan_Withdrawal = new PanWithdrawal(this);
        add(Pan_Withdrawal);
        Pan_Withdrawal.setVisible(false);

        Pan_Login = new PanLogin(this);
        add(Pan_Login);
        Pan_Login.setVisible(false);

        Pan_Loan = new PanLoan(this);
        add(Pan_Loan);
        Pan_Loan.setVisible(false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Btn_ViewAccount) {
            display("ViewAccount");
            Pan_ViewAccount.updateAccounts();
        } else if (e.getSource() == Btn_Transfer) {
            display("Transfer");
            Pan_Transfer.updateAccounts();
        } else if (e.getSource() == Btn_Login) {
            if (token != null) {
                display("Main");
            } else {
                display("Login");
            }
        } else if (e.getSource() == Btn_Deposite) {
            display("Deposite");
            Pan_Deposite.updateAccounts();
        } else if (e.getSource() == Btn_Withdrawal) {
            display("Withdrawal");
            Pan_Withdrawal.updateAccounts();
        } else if (e.getSource() == Btn_Loan) {
            display("Loan");
        } else if (e.getSource() == Btn_EnlargeText) {
            enlargeButtonText();
        }
    }

    private void enlargeButtonText() {
        Font largeFont = new Font("맑은 고딕", Font.BOLD, 20);
        Btn_ViewAccount.setFont(largeFont);
        Btn_Transfer.setFont(largeFont);
        Btn_Login.setFont(largeFont);
        Btn_Deposite.setFont(largeFont);
        Btn_Withdrawal.setFont(largeFont);
        Btn_Loan.setFont(largeFont);
    }

    public void display(String viewName) {
        if (token == null) {
            if (!viewName.equals("Login") && !viewName.equals("Main")) {
                JOptionPane.showMessageDialog(null, "카드를 투입하거나 로그인하세요.", "ERROR_MESSAGE",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        SetFrameUI(false);
        switch (viewName) {
            case "ViewAccount" -> Pan_ViewAccount.setVisible(true);
            case "Transfer" -> Pan_Transfer.setVisible(true);
            case "Deposite" -> Pan_Deposite.setVisible(true);
            case "Withdrawal" -> Pan_Withdrawal.setVisible(true);
            case "Login" -> Pan_Login.setVisible(true);
            case "Loan" -> Pan_Loan.setVisible(true);
            case "Main" -> SetFrameUI(true);
            case " EnlargeText" -> Btn_EnlargeText.setVisible(true);

        }
    }

    public void reset() {
        token = null;
        Pan_ViewAccount.setVisible(false);
        Pan_Transfer.setVisible(false);
        Pan_Deposite.setVisible(false);
        Pan_Withdrawal.setVisible(false);
        Pan_Login.setVisible(false);
        Pan_Loan.setVisible(false);
        SetFrameUI(true);
        JOptionPane.showMessageDialog(null, "서버에 연결할 수 없습니다.", "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    void SetFrameUI(Boolean bOn) {
        Label_Title.setVisible(bOn);
        Btn_ViewAccount.setVisible(bOn);
        Btn_Transfer.setVisible(bOn);
        Btn_Login.setVisible(bOn);
        Btn_Deposite.setVisible(bOn);
        Btn_Withdrawal.setVisible(bOn);
        Btn_Loan.setVisible(bOn);
        Label_Image.setVisible(bOn);
        Btn_EnlargeText.setVisible(bOn);
    }

    public static void main(String[] args) {
        new ATMMain();
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String filePath) {
            try {
                backgroundImage = ImageIO.read(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
