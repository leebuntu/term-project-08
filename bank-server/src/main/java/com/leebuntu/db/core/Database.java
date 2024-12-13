package com.leebuntu.db.core;

import com.leebuntu.db.core.file.DataFileManager;
import com.leebuntu.db.query.Query;
import com.leebuntu.db.query.QueryParser;
import com.leebuntu.db.query.QueryResult;
import com.leebuntu.db.query.enums.QueryStatus;
import com.leebuntu.db.storage.Metadata;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class Database {
    private String rootPath;
    private final String dbName;

    private final CacheManager cacheManager;
    private final DataFileManager dataFileManager;
    private final DataManager dataManager;
    private final QueryParser queryParser;

    private Metadata metaData;
    private Integer metaDataSize;

    public Database(String rootPath, String dbName) throws IOException {
        this.rootPath = rootPath;
        this.dbName = dbName;

        readMetadata();

        this.cacheManager = new CacheManager();
        this.dataFileManager = new DataFileManager(rootPath, dbName, cacheManager, metaData, metaDataSize);
        this.dataManager = new DataManager(dataFileManager);
        this.queryParser = QueryParser.getInstance();

        dataFileManager.compactEmptySpace();

        initCache();
    }

    /**
     * 캐시 초기화, 반드시 필요!
     * 인덱스 캐싱과, 인덱스와 DB 파일에서의 오프셋을 저장
     * 
     * @throws IOException
     */
    private void initCache() throws IOException {
        Map<String, Map<Object, Long>> indexMap = dataFileManager.getIndexMap();
        for (Map.Entry<String, Map<Object, Long>> entry : indexMap.entrySet()) {
            cacheManager.initPKCache(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 쿼리 실행
     * 
     * @param query
     * @param params
     * @return
     */
    public QueryResult execute(String query, Object... params) {
        Query parsedQuery = queryParser.parse(query, Arrays.asList(params));
        switch (parsedQuery.getQueryType()) {
            case SELECT:
                return dataManager.select(parsedQuery);
            case INSERT:
                return dataManager.insert(parsedQuery);
            case UPDATE:
                return dataManager.update(parsedQuery);
            case DELETE:
                return dataManager.delete(parsedQuery);
            default:
                return new QueryResult(QueryStatus.FAILED);
        }
    }

    /**
     * DB 파일로부터 메타데이터를 읽어옴
     * 
     * @throws IOException
     */
    public void readMetadata() throws IOException {
        File file = new File(rootPath + File.separator + dbName + ".db");
        try (FileInputStream fis = new FileInputStream(file);
                DataInputStream dis = new DataInputStream(fis)) {
            this.metaDataSize = dis.readInt();
            this.metaData = new Metadata();
            byte[] metadataBytes = new byte[this.metaDataSize - 4];
            dis.readFully(metadataBytes);
            this.metaData.fromBytes(metadataBytes);
        }
    }

    /**
     * 데이터베이스 종료
     * 
     * @throws IOException
     */
    public void close() throws IOException {
        dataFileManager.close();
    }

}
