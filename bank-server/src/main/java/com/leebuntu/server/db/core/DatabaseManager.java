package com.leebuntu.server.db.core;

import java.io.IOException;
import java.util.HashMap;

public class DatabaseManager {
    private static HashMap<String, Database> dbMap = new HashMap<>();

    /**
     * Database 인스턴스가 없을 경우에만 새롭게 만들어서 반환
     * 
     * @param dbName
     * @return
     * @throws IOException
     */
    public static Database initDB(String rootPath, String dbName) {
        if (dbMap.containsKey(dbName)) {
            return dbMap.get(dbName);
        } else {
            try {
                Database db = new Database(rootPath, dbName);
                dbMap.put(dbName, db);
                return db;
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public static Database getDB(String dbName) {
        Database db = dbMap.get(dbName);
        if (db == null) {
            throw new IllegalArgumentException("Database not found: " + dbName);
        }
        return db;
    }
}
