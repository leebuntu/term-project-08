package com.leebuntu.server.db.storage;

import com.leebuntu.common.serialization.ByteConvertible;
import com.leebuntu.common.serialization.ByteInputProcessor;
import com.leebuntu.common.serialization.ByteOutputProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Metadata implements ByteConvertible {
    private int tableCount;
    private List<Table> tables;
    private List<Integer> tableSizes;

    public Metadata() {
        this.tables = new ArrayList<>();
        this.tableSizes = new ArrayList<>();
    }

    public void addTable(Table table) {
        this.tables.add(table);
        this.tableCount++;
    }

    public Table getTable(String tableName) {
        for (Table table : this.tables) {
            if (table.getTableName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }

    public int getTableAutoIncrementOffset(String tableName) {
        int offset = 8;
        for (int i = 0; i < this.tableCount; i++) {
            if (this.tables.get(i).getTableName().equals(tableName)) {
                for (int j = 0; j < i; j++) {
                    offset += 4;
                    offset += this.tableSizes.get(j);
                }
                offset += 4;
                return offset;
            }
        }
        return -1;
    }

    public List<Table> getTables() {
        return this.tables;
    }

    /**
     * 메타데이터를 바이트 배열로 변환
     * [table_count(4)][table_length(4)][table_bytes(n)]...
     * 
     * @return
     * @throws IOException
     */
    @Override
    public byte[] toBytes() throws IOException {
        return ByteOutputProcessor.processOutput(dos -> {
            dos.writeInt(this.tableCount);

            for (Table table : this.tables) {
                byte[] tableBytes = table.toBytes();
                this.tableSizes.add(tableBytes.length);
                dos.writeInt(tableBytes.length);
                dos.write(tableBytes);
            }
        });
    }

    /**
     * 바이트 배열을 읽어 내부 필드에 저장
     * [table_count(4)][table_length(4)][table_bytes(n)]...
     * 
     * @param bytes
     * @throws IOException
     * @throws ArrayIndexOutOfBoundsException
     */
    @Override
    public void fromBytes(byte[] bytes) throws IOException, ArrayIndexOutOfBoundsException {
        ByteInputProcessor.processInput(bytes, dis -> {
            this.tableCount = dis.readInt();
            for (int i = 0; i < this.tableCount; i++) {
                int tableLength = dis.readInt();
                this.tableSizes.add(tableLength);
                byte[] tableBytes = new byte[tableLength];
                dis.readFully(tableBytes);

                Table table = new Table();
                table.fromBytes(tableBytes);
                this.tables.add(table);
            }
        });
    }

}
