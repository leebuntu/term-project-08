package com.leebuntu.db.storage;

import com.leebuntu.db.storage.enums.ColumnType;
import com.leebuntu.serialization.ByteConvertible;
import com.leebuntu.serialization.ByteInputProcessor;
import com.leebuntu.serialization.ByteOutputProcessor;

import java.io.IOException;

public class Column implements ByteConvertible {
    private ColumnType columnType;
    private boolean isPrimaryKey;
    private int columnSize;
    private String columnName;

    public Column(ColumnType columnType, boolean isPrimaryKey, int columnSize, String columnName) {
        this.columnType = columnType;
        this.isPrimaryKey = isPrimaryKey;
        this.columnSize = columnSize;
        this.columnName = columnName;
    }

    public Column(ColumnType columnType, int columnSize, String columnName) {
        this(columnType, false, columnSize, columnName);
    }

    public Column(ColumnType columnType, boolean isPrimaryKey, String columnName) {
        this(columnType, isPrimaryKey, columnType.getSize(), columnName);
    }

    public Column(ColumnType columnType, String columnName) {
        this(columnType, false, columnType.getSize(), columnName);
    }

    public Column() {
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public ColumnType getColumnType() {
        return this.columnType;
    }

    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public boolean isPrimaryKey() {
        return this.isPrimaryKey;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public int getColumnSize() {
        return this.columnSize;
    }

    public byte[] getDefaultValue() {
        return new byte[this.columnSize];
    }

    /**
     * 컬럼 정보를 바이트 배열로 변환
     * [column_type_ordinal(4)][is_primary_key(1)][column_size(4)][column_name(n)]
     * 
     * @return
     * @throws IOException
     */
    @Override
    public byte[] toBytes() throws IOException {
        return ByteOutputProcessor.processOutput(dos -> {
            dos.writeInt(this.columnType.ordinal());
            dos.writeBoolean(this.isPrimaryKey);
            dos.writeInt(this.columnSize);
            dos.writeUTF(this.columnName);
        });
    }

    /**
     * 바이트 배열을 읽어 내부 필드에 저장
     * [column_type_ordinal(4)][is_primary_key(1)][column_size(4)][column_name(n)]
     * 
     * @param bytes
     * @throws IOException
     * @throws ArrayIndexOutOfBoundsException
     */
    @Override
    public void fromBytes(byte[] bytes) throws IOException, ArrayIndexOutOfBoundsException {
        ByteInputProcessor.processInput(bytes, dis -> {
            this.columnType = ColumnType.values()[dis.readInt()];
            this.isPrimaryKey = dis.readBoolean();
            this.columnSize = dis.readInt();
            this.columnName = dis.readUTF();
        });
    }
}
