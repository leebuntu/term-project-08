package com.leebuntu.server.db.storage;

import com.leebuntu.common.serialization.ByteConvertible;
import com.leebuntu.common.serialization.ByteInputProcessor;
import com.leebuntu.common.serialization.ByteOutputProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Table implements ByteConvertible {
    private int autoIncrement;
    private String tableName;
    private int columnCount;
    private List<Column> columns;
    private int pkIndex;

    public Table() {
        this.columns = new ArrayList<>();
    }

    public Table(String tableName) {
        this.tableName = tableName;
        this.columns = new ArrayList<>();
    }

    public int getAutoIncrement() {
        return this.autoIncrement++;
    }

    public void addColumn(Column column) {
        this.columns.add(column);
        this.columnCount++;
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public int getPKIndex() {
        return this.pkIndex;
    }

    public boolean isPrimaryKey(int index) {
        if (index == -1) {
            return false;
        }
        return this.columns.get(index).isPrimaryKey();
    }

    public int getColumnIndex(String columnName) {
        for (int i = 0; i < this.columnCount; i++) {
            if (this.columns.get(i).getColumnName().equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public Class<?> getColumnType(int index) {
        return this.columns.get(index).getColumnType().getType();
    }

    public Column getColumn(int index) {
        return this.columns.get(index);
    }

    @Override
    public byte[] toBytes() throws IOException {
        return ByteOutputProcessor.processOutput(dos -> {
            dos.writeInt(this.autoIncrement);
            dos.writeUTF(this.tableName);
            dos.writeInt(this.columnCount);

            int index = 0;
            for (Column column : this.columns) {
                if (column.isPrimaryKey()) {
                    this.pkIndex = index;
                }
                byte[] columnBytes = column.toBytes();
                dos.writeInt(columnBytes.length);
                dos.write(columnBytes);
                index++;
            }
            dos.writeInt(this.pkIndex);
        });
    }

    @Override
    public void fromBytes(byte[] bytes) throws IOException, ArrayIndexOutOfBoundsException {
        ByteInputProcessor.processInput(bytes, dis -> {
            this.autoIncrement = dis.readInt();
            this.tableName = dis.readUTF();
            this.columnCount = dis.readInt();

            for (int i = 0; i < this.columnCount; i++) {
                int columnLength = dis.readInt();
                byte[] columnBytes = new byte[columnLength];
                dis.readFully(columnBytes);

                Column column = new Column();
                column.fromBytes(columnBytes);
                this.columns.add(column);
            }

            this.pkIndex = dis.readInt();
        });
    }

}
