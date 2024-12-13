package com.leebuntu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BankManagerButtonPanel implements ActionListener {
    private JPanel buttonPanel;
    private BankManagerTablePanel tablePanel;
    private boolean isLoggedIn = false;// 로그인 여부를 저장하는 변수
    private String[] accountOptions = { "당좌계좌", "저축계좌" };
    private String token;

    CardLayout cardLayout;
    JPanel mainPanel;

    String[] allCustomer = { "로그인", "사용자 불러오기" };
    String[] account = { "모든 계좌", "계좌 불러오기" };
    String[] add = { "사용자 추가하기", "계좌 추가하기" };
    String[] delete = { "사용자 삭제하기", "계좌 삭제하기" };

    public BankManagerButtonPanel(BankManagerTablePanel tablePanel, CardLayout cardLayout, JPanel mainPanel) {
        this.tablePanel = tablePanel;
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;

        initializeButtons();
    }

    private void initializeButtons() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // 버튼 추가하기
        buttonPanel.add(createSection("BankManager", allCustomer, 1));
        buttonPanel.add(createSection("Account", account, 1));
        buttonPanel.add(createSection("Add", add, 1));
        buttonPanel.add(createSection("Delete", delete, 1));
    }

    private JPanel createSection(String sectionName, String[] buttonLabels, int rows) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 버튼 종류를 나타내는 레이블
        JLabel sectionLabel = new JLabel(sectionName, JLabel.CENTER);
        sectionLabel.setBackground(Color.LIGHT_GRAY);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        sectionLabel.setOpaque(true);
        sectionPanel.add(sectionLabel, BorderLayout.NORTH);

        // 버튼
        JPanel buttons = new JPanel(new GridLayout(1, buttonLabels.length, 10, 10));
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(this);
            buttons.add(button);
        }
        sectionPanel.add(buttons, BorderLayout.CENTER);
        return sectionPanel;
    }

    // TODO: 서버와 인증이 안될 경우
    private void showServerConnectionErrorDialog() {
        JOptionPane.showMessageDialog(
                null,
                "서버와의 연결이 실패했습니다.\n네트워크 상태를 확인하세요.",
                "서버 연결 오류",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        String buttonText = clickedButton.getText();

        if (!isLoggedIn && !buttonText.equals("로그인")) {
            JOptionPane.showMessageDialog(null, "먼저 로그인을 해주세요.", "로그인 필요",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (buttonText) {
            case "로그인":
                JTextField usernameField = new JTextField();
                JPasswordField passwordField = new JPasswordField();

                Object[] message = {
                        "아이디:", usernameField,
                        "비밀번호:", passwordField
                };

                int option = JOptionPane.showConfirmDialog(
                        null,
                        message,
                        "로그인",
                        JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());

                    // 간단한 입력값 검증
                    if (username.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "아이디와 비밀번호를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    } else {
                        String result = BankManagerConnector.login(username, password);
                        if (result != null) {
                            JOptionPane.showMessageDialog(null, "로그인 성공: " + username, "로그인 완료",
                                    JOptionPane.INFORMATION_MESSAGE);
                            token = result;
                            tablePanel.token = token;
                            isLoggedIn = true;
                        } else {
                            JOptionPane.showMessageDialog(null, "로그인 실패: " + username, "로그인 오류",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "로그인이 취소되었습니다.", "취소", JOptionPane.WARNING_MESSAGE);
                }
                break;

            case "사용자 불러오기":
                ArrayList<String[]> allUsers = tablePanel.getCustomers();
                if (allUsers != null && !allUsers.isEmpty()) {
                    // 데이터를 JTable에 업데이트
                    String[][] data = allUsers.toArray(new String[0][]);
                    tablePanel.updateTable(data, tablePanel.columnNames);
                    cardLayout.show(mainPanel, "TableView"); // 테이블 패널로 전환

                } else {
                    JOptionPane.showMessageDialog(null, "서버에서 사용자 데이터를 가져오지 못했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "모든 계좌":
                ArrayList<String[]> allAccounts = tablePanel.getAccounts();
                if (allAccounts != null && !allAccounts.isEmpty()) {
                    // 계좌 데이터를 JTable에 업데이트
                    tablePanel.updateAccountTable(allAccounts);
                    cardLayout.show(mainPanel, "TableView");// 테이블 패널로 전환
                } else {
                    JOptionPane.showMessageDialog(null, "서버에서 계좌 데이터를 가져오지 못했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
                break;

            case "사용자 추가하기":
                // 사용자 추가를 위한 입력 다이얼로그
                String[] prompts = { "CustomerID", "Password", "Name", "Address", "Phone" };
                String[] userData = new String[prompts.length];
                for (int i = 0; i < prompts.length; i++) {
                    userData[i] = JOptionPane.showInputDialog(null, prompts[i] + "을(를) 입력하세요:", "사용자 추가",
                            JOptionPane.PLAIN_MESSAGE);
                    if (userData[i] == null || userData[i].trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "모든 값을 입력해야 합니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                tablePanel.addUser(userData);
                JOptionPane.showMessageDialog(null, "새로운 사용자가 추가되었습니다.", "추가 완료", JOptionPane.INFORMATION_MESSAGE);
                break;

            // TODO: 서버에서 삭제하는 기능이랑 연동하기
            case "사용자 삭제하기":
                // 선택된 사용자를 삭제
                int selectedRow = tablePanel.table.getSelectedRow(); // 선택된 행 가져오기
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "삭제할 사용자를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                } else {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "선택한 사용자를 삭제하시겠습니까?",
                            "사용자 삭제 확인",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        tablePanel.deleteSelectedRow(); // TODO: 테이블과 데이터에서 행 삭제, 서버에서도 사용자 데이터 삭제
                        JOptionPane.showMessageDialog(null, "사용자가 삭제되었습니다.");
                    }
                }
                break;

            // TODO: 계좌를 JTable에 표시를 안함으로 서버에서 JTable에 선택된 사람과 서버와 연결
            case "계좌 불러오기":
                int selectedUser = tablePanel.getSelectedUser();
                if (selectedUser != -1) {
                    int selectedCustomerRow = tablePanel.table.getSelectedRow();
                    String customerId = (String) tablePanel.table.getValueAt(selectedCustomerRow, 2); // 고객 ID는
                                                                                                      // 열 인덱스 2

                    // 고객 ID에 해당하는 계좌 정보 가져오기
                    ArrayList<String[]> selectedAccounts = tablePanel.getSeleteCustomerAccount(customerId);

                    // 계좌 정보를 테이블에 업데이트
                    tablePanel.updateAccountTable(selectedAccounts);

                    // 다른 화면으로 전환 (예: 계좌 정보 화면)
                    cardLayout.show(mainPanel, "TableView");
                } else {
                    // 선택된 사용자가 없는 경우
                    JOptionPane.showMessageDialog(null, "사용자를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                }
                break;

            // TODO: 사용자를 선택하면 정보를 서버로 보내고 서버에서는 새로운 계좌를 받을 준비
            case "계좌 추가하기":

                selectedUser = tablePanel.getSelectedUser();
                // 선택 결과 출력
                if (selectedUser == -1) {
                    JOptionPane.showMessageDialog(null, "사용자를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                } else {
                    String selectedOption = (String) JOptionPane.showInputDialog(
                            null,
                            "계좌 종류를 고르세요.",
                            "계좌 추가",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            accountOptions,
                            accountOptions[0] // 기본 선택값
                    );
                    if (selectedOption != null && selectedOption == "당좌계좌") {
                        String[] accountPrompt = { "account_number", "total_balance",
                                "available_balance", "linked_savings_id" };
                        String[] accountData = new String[accountPrompt.length];
                        for (int i = 0; i < accountPrompt.length; i++) {
                            accountData[i] = JOptionPane.showInputDialog(null, accountPrompt[i] + "을(를) 입력하세요:",
                                    "계좌 추가",
                                    JOptionPane.PLAIN_MESSAGE);
                            if (accountData[i] == null || accountData[i].trim().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "모든 값을 입력해야 합니다.", "입력 오류",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        tablePanel.addAccount(selectedUser, accountData);
                    } else {
                        String[] accountPrompt = { "account_number", "total_balance",
                                "available_balance", "interest_rate", "max_transfer_amount_to_checking" };
                        String[] accountData = new String[accountPrompt.length];
                        for (int i = 0; i < accountPrompt.length; i++) {
                            accountData[i] = JOptionPane.showInputDialog(null, accountPrompt[i] + "을(를) 입력하세요:",
                                    "사용자 추가",
                                    JOptionPane.PLAIN_MESSAGE);
                            if (accountData[i] == null || accountData[i].trim().isEmpty()) {
                                JOptionPane.showMessageDialog(null, "모든 값을 입력해야 합니다.", "입력 오류",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        tablePanel.addAccount(selectedUser, accountData);
                    }
                }
                break;

            // TODO: 사용자를 선택하면 사용자 정보를 서버에 보내고 그에 맞는 사용자의 계좌들을 서버에서 받아온다.
            case "계좌 삭제하기":
                int selectedRowForDelete = tablePanel.table.getSelectedRow();// 선택된 행 가져오기
                if (selectedRowForDelete == -1) {
                    JOptionPane.showMessageDialog(null, "사용자를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                } else
                    // TODO: 받아온 계좌를 화면에 표시, 그리고 계좌를 골라 삭제 버튼 클릭 -> 서버에서 해당 계좌 삭제
                    break;
        }
    }

    public JPanel getPanel() {
        return buttonPanel;
    }
}
