package com.leebuntu.manager;

import com.leebuntu.common.banking.BankingResult;
import com.leebuntu.common.banking.BankingResult.BankingResultType;
import com.leebuntu.common.banking.account.Account;
import com.leebuntu.common.banking.account.AccountType;
import com.leebuntu.common.banking.customer.Customer;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BankManagerTablePanel {
    private JPanel tablePanel;
    JTable table;
    DefaultTableModel tableModel;
    private int selectedIndex = -1;
    private int selectedId = -1;
    private String selectedAccountNumber = null;
    private int currentMode = 0; // 0: 초기, 1: 사용자 리스트 2: 계좌 리스트
    private ArrayList<String[]> customerData;
    boolean isEditable = false; // 추가: 테이블 수정 가능 여부
    String token;

    private List<List<Object>> originalTable;

    String[] columnNames = { "ID", "Name", "CustomerID", "Password", "Address", "Phone", "신용점수" };
    String[] accountColumns = { "ID", "고객 ID", "계좌 번호", "잔액", "출금 가능 금액", "계좌 개설일", "연결 예금 계좌", "이자율", "최대 자동 이체 금액" };

    public BankManagerTablePanel() {
        customerData = new ArrayList<>(); // 초기화
        initializeTable();
    }

    private void initializeTable() {
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                if (row != 0) {
                    return isEditable; // 수정 가능 여부에 따라 설정
                }
                return false;
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
                if (rowIndex != -1 && rowIndex != 0) {
                    // Name 열의 값 저장
                    selectedIndex = rowIndex;
                    selectedId = Integer.parseInt((String) table.getValueAt(rowIndex, 0));
                    selectedAccountNumber = (String) table.getValueAt(rowIndex, 2);
                } else {
                    // 선택이 해제된 경우
                    selectedId = -1;
                    selectedIndex = -1;
                    selectedAccountNumber = null;
                }
            }
        });

        // 수정버튼 패널 생성
        JPanel fixPanel = new JPanel();
        JButton ableFixButton = new JButton("수정");
        ableFixButton.setSize(10, 5);
        ableFixButton.addActionListener((e) -> {
            if (isEditable) {
                JOptionPane.showMessageDialog(null, "이미 수정 모드입니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            isEditable = true;

            this.originalTable = new ArrayList<>();

            for (int row = 0; row < table.getRowCount(); row++) {
                List<Object> rowData = new ArrayList<>();
                for (int col = 0; col < table.getColumnCount(); col++) {
                    rowData.add(table.getValueAt(row, col));
                }
                this.originalTable.add(rowData);
            }
            table.repaint();
        });

        JButton inableFixButton = new JButton("수정완료");
        inableFixButton.setSize(10, 5);
        inableFixButton.addActionListener((e) -> {
            if (!isEditable) {
                JOptionPane.showMessageDialog(null, "수정 모드가 아닙니다.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            isEditable = false;

            for (int row = 0; row < table.getRowCount(); row++) {
                for (int col = 0; col < table.getColumnCount(); col++) {
                    Object original = originalTable.get(row).get(col);
                    Object current = table.getValueAt(row, col);
                    if (!Objects.equals(original, current)) {
                        String[] rowData = new String[table.getColumnCount()];
                        for (int i = 0; i < table.getColumnCount(); i++) {
                            rowData[i] = (String) table.getValueAt(row, i);
                        }
                        if (currentMode == 1) {
                            updateCustomer(rowData);
                        } else if (currentMode == 2) {
                            updateAccount(rowData);
                        }
                        break;
                    }
                }
            }
            table.repaint();
        });

        fixPanel.add(ableFixButton);
        fixPanel.add(inableFixButton);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 440));

        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("정보"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(fixPanel, BorderLayout.SOUTH);
    }

    // ID, Name 열 크기 조정 메소드
    private void adjustColumnWidths() {
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
    }

    public BankingResult updateCustomer(String[] customerRowData) {
        Customer customer = new Customer();
        customer.setId(Integer.parseInt(customerRowData[0]));
        customer.setName(customerRowData[1]);
        customer.setCustomerId(customerRowData[2]);
        customer.setPassword(customerRowData[3]);
        customer.setAddress(customerRowData[4]);
        customer.setPhone(customerRowData[5]);
        customer.setCreditScore(Integer.parseInt(customerRowData[6]));
        return BankManagerConnector.updateCustomer(token, customer);
    }

    public BankingResult updateAccount(String[] accountRowData) {
        Account account = new Account();
        account.setId(Integer.parseInt(accountRowData[0]));
        account.setCustomerId(Integer.parseInt(accountRowData[1]));
        account.setAccountNumber(accountRowData[2]);
        account.setTotalBalance(Long.parseLong(accountRowData[3]));
        account.setAvailableBalance(Long.parseLong(accountRowData[4]));
        account.setOpenDate(Instant.parse(accountRowData[5]).toEpochMilli());
        account.setLinkedSavingsAccountNumber(accountRowData[6]);
        account.setInterestRate(Double.parseDouble(accountRowData[7]));
        account.setMaxTransferAmountToChecking(Long.parseLong(accountRowData[8]));
        return BankManagerConnector.updateAccount(token, account);
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
                    customer.getPassword(),
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

    ArrayList<String[]> getAccountsByCustomerId(int customerId) {
        ArrayList<String[]> accountData = new ArrayList<>();
        BankingResult result = BankManagerConnector.getAccountsByCustomerId(token, customerId);
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

    // 사용자 정보를 누르면 다른 화면에서 다시 사용자 정보들이 있는 화면으로 오게 하는 메서드
    public void updateTable(String[][] data, String[] columnNames, int currentMode) {
        tableModel.setDataVector(data, columnNames);
        adjustColumnWidths();
        this.currentMode = currentMode;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    // 사용자 추가
    public BankingResult addUser(String[] userData) {
        return BankManagerConnector.createCustomer(token, userData[0], userData[1], userData[2], userData[3],
                userData[4]);
    }

    public BankingResult addAccount(int userId, String[] accountData) {
        if (accountData.length == 4) { // 당좌 계좌
            Account account = new Account();
            account.setCustomerId(userId);
            account.setAccountNumber(accountData[0]);
            account.setTotalBalance(Long.parseLong(accountData[1]));
            account.setAvailableBalance(Long.parseLong(accountData[2]));
            account.setAccountType(AccountType.CHECKING);
            account.setLinkedSavingsAccountNumber(accountData[3]);
            return BankManagerConnector.createCheckingAccount(token, account);
        } else { // 저축 계좌
            Account account = new Account();
            account.setCustomerId(userId);
            account.setAccountNumber(accountData[0]);
            account.setTotalBalance(Long.parseLong(accountData[1]));
            account.setAvailableBalance(Long.parseLong(accountData[2]));
            account.setAccountType(AccountType.SAVINGS);
            account.setInterestRate(Double.parseDouble(accountData[3]));
            account.setMaxTransferAmountToChecking(Long.parseLong(accountData[4]));
            return BankManagerConnector.createSavingsAccount(token, account);
        }
    }

    // JTable에서 행이 클릭이 되었는지 확인
    // 클릭된 정보의 이름 반환(계좌 주인)
    public int getSelectedId() {
        return selectedId;
    }

    public String getSelectedAccountNumber() {
        return selectedAccountNumber;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    // 수정된 사용자/계좌 셀을 저장하는 메서드
    private void updateCustomerData(int row, int column, String updatedValue) {
        if (row < customerData.size() && column < columnNames.length) {
            customerData.get(row)[column] = updatedValue;
            // 서버에 뎐결해서 값을 변경 -- > 사용자 불러오기를 누르면 새로 변경된 값으로 불러옴
        }
    }

    public JPanel getPanel() {
        return tablePanel;
    }

    public BankingResult deleteSelectedAccount(String accountNumber) {
        return BankManagerConnector.deleteAccount(token, accountNumber);
    }

    public BankingResult deleteSelectedCustomer(int customerId) {
        return BankManagerConnector.deleteCustomer(token, customerId);
    }

}
