package com.leebuntu.server.db.query;

import com.leebuntu.server.db.query.enums.QueryStatus;
import com.leebuntu.server.db.storage.Column;
import com.leebuntu.server.db.util.ConvertUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryResult {
    private List<LinkedHashMap<Column, byte[]>> result;
    private QueryStatus queryStatus;
    private int currentRows = -1;
    private Object lastInsertId = null;

    public QueryResult() {
    }

    public QueryResult(QueryStatus queryStatus) {
        this.queryStatus = queryStatus;
        if (this.queryStatus != QueryStatus.SUCCESS) {
            this.currentRows = -2;
        }
    }

    public QueryResult(QueryStatus queryStatus, Object lastInsertId) {
        this.queryStatus = queryStatus;
        this.lastInsertId = lastInsertId;
        this.currentRows = -2;
    }

    public void setResult(List<LinkedHashMap<Column, byte[]>> result) {
        this.result = result;
        if (this.queryStatus != QueryStatus.SUCCESS && this.queryStatus != null) {
            this.currentRows = -2;
        }
    }

    public void setQueryStatus(QueryStatus queryStatus) {
        this.queryStatus = queryStatus;
        if (this.queryStatus != QueryStatus.SUCCESS) {
            this.currentRows = -2;
        }
    }

    public QueryStatus getQueryStatus() {
        return this.queryStatus;
    }

    public List<LinkedHashMap<Column, byte[]>> getResult() {
        return this.result;
    }

    public void setLastInsertId(Object lastInsertId) {
        this.lastInsertId = lastInsertId;
    }

    public Object getLastInsertId() {
        return this.lastInsertId;
    }

    public boolean next() {
        if (this.currentRows == -2) {
            return false;
        }
        if (currentRows < result.size() - 1) {
            currentRows++;
            return true;
        }
        return false;
    }

    public int getRowCount() {
        if (this.currentRows == -2) {
            return 0;
        }
        return result.size();
    }

    public List<Object> getCurrentRow() {
        if (this.currentRows == -2) {
            return null;
        }
        if (currentRows >= result.size()) {
            return null;
        }
        if (this.currentRows == -1) {
            this.currentRows++;
        }

        Map<Column, byte[]> row = result.get(currentRows);

        List<Object> result = new ArrayList<>();

        for (Map.Entry<Column, byte[]> entry : row.entrySet()) {
            Column column = entry.getKey();
            Object value = null;

            try {
                value = ConvertUtil.bytesToColumns(entry.getValue(), column);
            } catch (IOException e) {
                e.printStackTrace();
            }

            result.add(column.getColumnType().getType().cast(value));
        }

        return result;
    }
}
