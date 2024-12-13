package com.leebuntu.db.storage.enums;

public enum ColumnType {
    INT(Integer.class, 4),
    LONG(Long.class, 8),
    DOUBLE(Double.class, 8),
    VARCHAR(String.class, 0);

    private final Class<?> type;
    private final int size;

    private ColumnType(Class<?> type, int size) {
        this.type = type;
        this.size = size;
    }

    public Class<?> getType() {
        return type;
    }

    public int getSize() {
        return size;
    }
}
