package com.leebuntu.manager;

import javax.swing.*;

import com.leebuntu.common.banking.BankingResult;
import com.leebuntu.common.banking.BankingResult.BankingResultType;

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

    private void refreshUserList() {
        ArrayList<String[]> allUsers = tablePanel.getCustomers();
        if (allUsers != null) {
            String[][] data = new String[allUsers.size() + 1][allUsers.size() == 0 ? 2 : allUsers.get(0).length];
            data[0][0] = "총 유저 수";
            data[0][1] = String.valueOf(allUsers.size());
            for (int i = 0; i < allUsers.size(); i++) {
                data[i + 1] = allUsers.get(i);
            }

            tablePanel.updateTable(data, tablePanel.columnNames, 1);
            cardLayout.show(mainPanel, "TableView"); // 테이블 패널로 전환

        } else {
            JOptionPane.showMessageDialog(null, "서버에서 사용자 데이터를 가져오지 못했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAccountList() {
        ArrayList<String[]> allAccounts = tablePanel.getAccounts();
        if (allAccounts != null) {
            String[][] data = new String[allAccounts.size() + 1][allAccounts.size() == 0 ? 2
                    : allAccounts.get(0).length];
            data[0][0] = "총 보유 잔고";
            data[0][1] = String.valueOf(allAccounts.stream().map(s -> s[3]).mapToLong(Long::parseLong).sum());
            for (int i = 0; i < allAccounts.size(); i++) {
                data[i + 1] = allAccounts.get(i);
            }
            tablePanel.updateTable(data, tablePanel.accountColumns, 2);
            cardLayout.show(mainPanel, "TableView");// 테이블 패널로 전환
        } else {
            JOptionPane.showMessageDialog(null, "서버에서 계좌 데이터를 가져오지 못했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
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
                        BankingResult result = BankManagerConnector.login(username, password);
                        if (result.getType() == BankingResultType.SUCCESS) {
                            JOptionPane.showMessageDialog(null, "로그인 성공: " + username, "로그인 완료",
                                    JOptionPane.INFORMATION_MESSAGE);
                            token = (String) result.getData();
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
                refreshUserList();
                break;

            case "모든 계좌":
                refreshAccountList();
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
                BankingResult result = tablePanel.addUser(userData);
                if (result.getType() == BankingResultType.SUCCESS) {
                    JOptionPane.showMessageDialog(null, "새로운 사용자가 추가되었습니다.", "추가 완료", JOptionPane.INFORMATION_MESSAGE);
                    refreshUserList();
                } else {
                    JOptionPane.showMessageDialog(null, "사용자 추가 실패: " + result.getMessage(), "추가 오류",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;

            // TODO: 서버에서 삭제하는 기능이랑 연동하기
            case "사용자 삭제하기":
                if (tablePanel.getCurrentMode() != 1) {
                    JOptionPane.showMessageDialog(null, "사용자를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int selectedId = tablePanel.getSelectedId();
                if (selectedId == -1) {
                    JOptionPane.showMessageDialog(null, "삭제할 사용자를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                } else {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "선택한 사용자를 삭제하시겠습니까?",
                            "사용자 삭제 확인",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        result = tablePanel.deleteSelectedCustomer(selectedId);
                        if (result.getType() == BankingResultType.SUCCESS) {
                            JOptionPane.showMessageDialog(null, "사용자가 삭제되었습니다.", "삭제 완료",
                                    JOptionPane.INFORMATION_MESSAGE);
                            refreshUserList();
                        } else {
                            JOptionPane.showMessageDialog(null, "사용자 삭제 실패: " + result.getMessage(), "삭제 오류",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                break;

            // TODO: 계좌를 JTable에 표시를 안함으로 서버에서 JTable에 선택된 사람과 서버와 연결
            case "계좌 불러오기":
                if (tablePanel.getCurrentMode() != 1) {
                    JOptionPane.showMessageDialog(null, "사용자를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                selectedId = tablePanel.getSelectedId();
                if (selectedId != -1) {
                    // 고객 ID에 해당하는 계좌 정보 가져오기
                    ArrayList<String[]> selectedAccounts = tablePanel.getAccountsByCustomerId(selectedId);

                    String[][] data = new String[selectedAccounts.size() + 1][selectedAccounts.size() == 0 ? 2
                            : selectedAccounts.get(0).length];
                    data[0][0] = "총 보유 잔고";
                    data[0][1] = String
                            .valueOf(selectedAccounts.stream().map(s -> s[3]).mapToLong(Long::parseLong).sum());
                    for (int i = 0; i < selectedAccounts.size(); i++) {
                        data[i + 1] = selectedAccounts.get(i);
                    }

                    // 계좌 정보를 테이블에 업데이트
                    tablePanel.updateTable(data, tablePanel.accountColumns, 2);

                    // 다른 화면으로 전환 (예: 계좌 정보 화면)
                    cardLayout.show(mainPanel, "TableView");
                } else {
                    // 선택된 사용자가 없는 경우
                    JOptionPane.showMessageDialog(null, "사용자를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                }
                break;

            // TODO: 사용자를 선택하면 정보를 서버로 보내고 서버에서는 새로운 계좌를 받을 준비
            case "계좌 추가하기":
                if (tablePanel.getCurrentMode() != 1) {
                    JOptionPane.showMessageDialog(null, "사용자를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                selectedId = tablePanel.getSelectedId();
                // 선택 결과 출력
                if (selectedId == -1) {
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
                                "available_balance", "linked_savings_account_number" };
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
                        accountData[3] = accountData[3].replaceAll("null", "");
                        result = tablePanel.addAccount(selectedId, accountData);
                        if (result.getType() == BankingResultType.SUCCESS) {
                            JOptionPane.showMessageDialog(null, "계좌가 추가되었습니다.", "추가 완료",
                                    JOptionPane.INFORMATION_MESSAGE);
                            refreshAccountList();
                        } else {
                            JOptionPane.showMessageDialog(null, "계좌 추가 실패: " + result.getMessage(), "추가 오류",
                                    JOptionPane.ERROR_MESSAGE);
                        }
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
                        result = tablePanel.addAccount(selectedId, accountData);
                        if (result.getType() == BankingResultType.SUCCESS) {
                            JOptionPane.showMessageDialog(null, "계좌가 추가되었습니다.", "추가 완료",
                                    JOptionPane.INFORMATION_MESSAGE);
                            refreshAccountList();
                        } else {
                            JOptionPane.showMessageDialog(null, "계좌 추가 실패: " + result.getMessage(), "추가 오류",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                break;

            // TODO: 사용자를 선택하면 사용자 정보를 서버에 보내고 그에 맞는 사용자의 계좌들을 서버에서 받아온다.
            case "계좌 삭제하기":
                if (tablePanel.getCurrentMode() != 2) {
                    JOptionPane.showMessageDialog(null, "계좌를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String selectedAccountNumber = tablePanel.getSelectedAccountNumber();// 선택된 행 가져오기
                if (selectedAccountNumber == null) {
                    JOptionPane.showMessageDialog(null, "사용자를 선택하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                } else {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "선택한 계좌를 삭제하시겠습니까?",
                            "계좌 삭제 확인",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        result = tablePanel.deleteSelectedAccount(selectedAccountNumber);
                        if (result.getType() == BankingResultType.SUCCESS) {
                            JOptionPane.showMessageDialog(null, "계좌가 삭제되었습니다.", "삭제 완료",
                                    JOptionPane.INFORMATION_MESSAGE);
                            refreshAccountList();
                        } else {
                            JOptionPane.showMessageDialog(null, "계좌 삭제 실패: " + result.getMessage(), "삭제 오류",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                break;
        }
    }

    public JPanel getPanel() {
        return buttonPanel;
    }
}
