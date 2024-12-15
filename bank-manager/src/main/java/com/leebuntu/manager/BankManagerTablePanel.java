package com.leebuntu.manager;

import com.leebuntu.banking.BankingResult;
import com.leebuntu.banking.BankingResult.BankingResultType;
import com.leebuntu.banking.account.Account;
import com.leebuntu.banking.account.AccountType;
import com.leebuntu.banking.customer.Customer;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BankManagerTablePanel {
    private JPanel tablePanel;
    JTable table;
    DefaultTableModel tableModel;
    DefaultTableModel accountModel;
    private int selectedUser = -1;
    private ArrayList<String[]> customerData;
    boolean isLoggedIn = false;
    boolean isEditable = false; // 추가: 테이블 수정 가능 여부
    String token;

    String[] columnNames = { "ID", "Name", "CustomerID", "Address", "Phone", "신용점수" };
    String[] accountColumns = { "ID", "고객 ID", "계좌 번호", "잔액", "출금 가능 금액", "계좌 개설일", "연결 예금 계좌", "이자율", "최대 자동 이체 금액" };

    public BankManagerTablePanel() {
        customerData = new ArrayList<>(); // 초기화
        initializeTable();
    }

    private void initializeTable() {
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return isEditable; // 수정 가능 여부에 따라 설정
            }
        };

        table = new JTable(tableModel);

        // 셀 변경을 감지하는 리스너
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int column = e.getColumn();
                    if (row != -1 && column != -1) {
                        String updatedValue = (String) tableModel.getValueAt(row, column);
                        updateCustomerData(row, column, updatedValue);
                    }
                }
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // 이벤트가 연속 호출되는 것을 방지
                int rowIndex = table.getSelectedRow();
                // 행이 선택될 경우
                if (rowIndex != -1) {
                    // Name 열의 값 저장
                    selectedUser = Integer.parseInt((String) table.getValueAt(rowIndex, 0));
                } else {
                    // 선택이 해제된 경우
                    selectedUser = -1;

                }
            }
        });

        // 수정버튼 패널 생성
        JPanel fixPanel = new JPanel();
        JButton ableFixButton = new JButton("수정가능");
        ableFixButton.setSize(10, 5);
        ableFixButton.addActionListener((e) -> {
            isEditable = true;
            table.repaint();
        });

        JButton inableFixButton = new JButton("수정불가");
        inableFixButton.setSize(10, 5);
        inableFixButton.addActionListener((e) -> {
            isEditable = false;
            table.repaint();
        });

        fixPanel.add(ableFixButton);
        fixPanel.add(inableFixButton);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 700));

        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("사용자 정보"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(fixPanel, BorderLayout.SOUTH);
    }

    // ID, Name 열 크기 조정 메소드
    private void adjustColumnWidths() {
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
    }

    ArrayList<String[]> getCustomers() {
        ArrayList<String[]> customerData = new ArrayList<>();
        BankingResult result = BankManagerConnector.getCustomers(token);
        if (result.getType() != BankingResultType.SUCCESS) {
            return null;
        }
        List<Customer> customers = (List<Customer>) result.getData();
        for (Customer customer : customers) {
            customerData.add(new String[] { String.valueOf(customer.getId()),
                    customer.getName(),
                    customer.getCustomerId(),
                    customer.getAddress(),
                    customer.getPhone(),
                    String.valueOf(customer.getCreditScore()) });
        }
        return customerData;
    }

    ArrayList<String[]> getAccounts() {
        ArrayList<String[]> accountData = new ArrayList<>();
        BankingResult result = BankManagerConnector.getAccounts(token);
        if (result.getType() != BankingResultType.SUCCESS) {
            return null;
        }
        List<Account> accounts = (List<Account>) result.getData();
        for (Account account : accounts) {
            accountData.add(new String[] { String.valueOf(account.getId()),
                    String.valueOf(account.getCustomerId()), account.getAccountNumber(),
                    String.valueOf(account.getTotalBalance()),
                    String.valueOf(account.getAvailableBalance()),
                    Instant.ofEpochMilli(account.getOpenDate()).toString(),
                    account.getLinkedSavingsAccountNumber(),
                    String.valueOf(account.getInterestRate()),
                    String.valueOf(account.getMaxTransferAmountToChecking()) });
        }

        return accountData;
    }

    // 사용자 불러오기에서 선택된 사용자의 계좌를 출력
    public ArrayList<String[]> getSeleteCustomerAccount(String customerId) {
        ArrayList<String[]> allAccounts = getAccounts();
        ArrayList<String[]> selectAccount = new ArrayList<>();

        for (String[] account : allAccounts) {
            if (account[1].equals(customerId)) { // 고객 ID로 필터링
                selectAccount.add(account);
            }
        }

        return selectAccount;
    }

    private String[][] convertTo2DArray(ArrayList<String[]> data) {
        if (data == null || data.isEmpty()) {
            return new String[0][];
        }
        return data.toArray(new String[0][]);
    }

    // 사용자 데이터를 테이블에 출력
    // 계좌 데이터를 테이블에 출력
    public void updateAccountTable(ArrayList<String[]> accountData) {
        String[][] data = convertTo2DArray(accountData);
        updateTable(data, accountColumns);
    }

    // 사용자 정보를 누르면 다른 화면에서 다시 사용자 정보들이 있는 화면으로 오게 하는 메서드
    public void updateTable(String[][] data, String[] columnNames) {
        tableModel.setDataVector(data, columnNames);
        adjustColumnWidths();
    }

    // 사용자 추가
    public void addUser(String[] userData) {
        BankManagerConnector.createCustomer(token, userData[0], userData[1], userData[2], userData[3], userData[4]);
    }

    public void addAccount(int userId, String[] accountData) {
        if (accountData.length == 4) { // 당좌 계좌
            Account account = new Account();
            account.setCustomerId(userId);
            account.setAccountNumber(accountData[0]);
            account.setTotalBalance(Long.parseLong(accountData[1]));
            account.setAvailableBalance(Long.parseLong(accountData[2]));
            account.setAccountType(AccountType.CHECKING);
            account.setLinkedSavingsAccountNumber(accountData[3]);
            BankManagerConnector.createCheckingAccount(token, account);
        } else { // 저축 계좌
            Account account = new Account();
            account.setCustomerId(userId);
            account.setAccountNumber(accountData[0]);
            account.setTotalBalance(Long.parseLong(accountData[1]));
            account.setAvailableBalance(Long.parseLong(accountData[2]));
            account.setAccountType(AccountType.SAVINGS);
            account.setInterestRate(Double.parseDouble(accountData[3]));
            account.setMaxTransferAmountToChecking(Long.parseLong(accountData[4]));
            BankManagerConnector.createSavingsAccount(token, account);
        }
    }

    // JTable에서 행이 클릭이 되었는지 확인
    // 클릭된 정보의 이름 반환(계좌 주인)
    public int getSelectedUser() {
        return selectedUser;
    }

    // 수정된 사용자/계좌 셀을 저장하는 메서드
    private void updateCustomerData(int row, int column, String updatedValue) {
        if (row < customerData.size() && column < columnNames.length) {
            customerData.get(row)[column] = updatedValue;
            // 서버에 뎐결해서 값을 변경 -- > 사용자 불러오기를 누르면 새로 변경된 값으로 불러옴
        }
    }

    // 지우고 싶은 사용자의 row를 선택하고 사용자 삭제를 누르면 해당 데이터 JTable패널에서 삭제
    // 필수 메소드
    // TODO: 서버에서 삭제 따로 진행
    public void deleteSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            selectedUser = -1;
        }
    }

    public JPanel getPanel() {
        return tablePanel;
    }
}
