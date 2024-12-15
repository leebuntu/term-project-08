package com.leebuntu.banking;

public class BankingResult {
    public enum BankingResultType {
        SUCCESS,
        FAILED,
        INTERNAL_ERROR,
        NOT_FOUND,
        NOT_AUTHORIZED,
        INVALID_REQUEST,
        INSUFFICIENT_FUNDS,
        NEED_TO_SAVINGS_ACCOUNT,
        DUPLICATED,
    }

    private BankingResultType type;
    private String message;
    private Object data;

    public BankingResult(BankingResultType type, String message) {
        this.type = type;
        this.message = message;
    }

    public BankingResult(BankingResultType type, String message, Object data) {
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public BankingResultType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public String getFineMessage() {
        String msg = this.type.name() + ": " + this.message;
        return msg;
    }

}
