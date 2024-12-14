package com.leebuntu.server.db.storage;

import com.leebuntu.server.db.util.Config;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseBuilder {
    private String databaseName;
    private Metadata metadata;
    private List<Table> tables;

    public DatabaseBuilder() {
        this.tables = new ArrayList<>();
    }

    /**
     * 데이터베이스 이름 설정
     * 
     * @param databaseName
     * @return
     */
    public DatabaseBuilder setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    /**
     * 테이블 추가
     * 
     * @param table
     * @return
     */
    public DatabaseBuilder addTable(Table table) {
        this.tables.add(table);
        return this;
    }

    /**
     * 메타데이터 생성 및 DB 파일 생성
     * 
     * @throws IOException
     */
    public boolean create() throws IOException {
        this.metadata = new Metadata();
        for (Table table : this.tables) {
            this.metadata.addTable(table);
        }

        File dbFile = new File(Config.DB_ROOT_PATH + File.separator + this.databaseName + ".db");
        if (!dbFile.exists()) {
            dbFile.createNewFile();
        } else {
            return false;
        }

        byte[] metadataBytes = this.metadata.toBytes();

        try (FileOutputStream fos = new FileOutputStream(dbFile);
                DataOutputStream dos = new DataOutputStream(fos)) {
            dos.writeInt(metadataBytes.length + 4);
            dos.write(metadataBytes);
            fos.flush();
        }

        return true;
    }
}
