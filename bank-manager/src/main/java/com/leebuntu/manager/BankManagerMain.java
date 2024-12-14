package com.leebuntu.manager;

import javax.swing.*;
import java.awt.*;

public class BankManagerMain extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Bank Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 500);

            // 카드 레이아웃을 사용하여 버튼을 누르면 그에 해당하는 내용이나 패널을 JTable이 있던 화면에 보여준다.
            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // 패널 생성 (테이블 패널, 버튼 패널)
            BankManagerTablePanel tablePanel = new BankManagerTablePanel();

            // 처음 빈 화면을 보여주는 패널
            JPanel nullPanel = new JPanel();
            JTable nulltable = new JTable();
            JScrollPane nullscrollPane = new JScrollPane(nulltable);
            nullscrollPane.setPreferredSize(new Dimension(600, 700));
            nullPanel.setBorder(BorderFactory.createTitledBorder("사용자 정보를 불러오세요"));
            nullPanel.add(nullscrollPane, BorderLayout.CENTER);

            // 카드레이아웃으로 되어있는 패널에 다른 패널들 추가
            mainPanel.add(nullPanel);
            mainPanel.add(tablePanel.getPanel(), "TableView");

            BankManagerButtonPanel buttonPanel = new BankManagerButtonPanel(tablePanel, cardLayout, mainPanel);
            // 토탈 패널에 모두 추가해 준다
            JPanel totalPanel = new JPanel(new BorderLayout());
            totalPanel.add(mainPanel, BorderLayout.CENTER);
            totalPanel.add(buttonPanel.getPanel(), BorderLayout.WEST);

            frame.add(totalPanel);
            frame.setVisible(true);
        });
    }
}
