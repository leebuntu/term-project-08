package com.leebuntu.server.db.query;

import com.leebuntu.server.db.query.enums.QueryType;

import java.util.ArrayList;
import java.util.List;

public class Query {
    private QueryType queryType;
    private List<String> targetColumnNames;
    private String tableName;
    private String whereColumnName;
    private ArrayList<Object> parameters;

    public Query() {
        this.parameters = new ArrayList<>();
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public QueryType getQueryType() {
        return this.queryType;
    }

    public void setTargetColumnNames(List<String> targetColumnNames) {
        this.targetColumnNames = targetColumnNames;
    }

    public List<String> getTargetColumnNames() {
        return this.targetColumnNames;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setWhereColumnName(String whereColumnName) {
        this.whereColumnName = whereColumnName;
    }

    public String getWhereColumnName() {
        return this.whereColumnName;
    }

    public Object getWhereKey() {
        return this.parameters.get(this.parameters.size() - 1);
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = new ArrayList<>(parameters);
    }

    public void addParameter(Object parameter) {
        this.parameters.add(parameter);
    }

    public List<Object> getParameters() {
        return this.parameters;
    }
}
