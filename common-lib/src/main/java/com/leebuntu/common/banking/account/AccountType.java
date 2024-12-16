package com.leebuntu.common.banking.account;

public enum AccountType {
    CHECKING("당좌계좌"),
    SAVINGS("저축계좌");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static AccountType fromDescription(String description) {
        for (AccountType type : values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid description: " + description);
    }
}
