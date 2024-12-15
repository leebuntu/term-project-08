package com.leebuntu.server.db.core.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.leebuntu.server.db.core.Database;
import com.leebuntu.server.db.query.Query;
import com.leebuntu.server.db.query.QueryParser;
import com.leebuntu.server.db.query.QueryResult;
import com.leebuntu.server.db.query.enums.QueryStatus;

public class Transaction {
    class TransactionOperation {
        private String query;
        private List<Object> params;

        public TransactionOperation(String query, List<Object> params) {
            this.query = query;
            this.params = params;
        }

        public String getQuery() {
            return query;
        }

        public List<Object> getParams() {
            return params;
        }
    }

    private List<TransactionOperation> operations = new ArrayList<>();
    private Database db;
    private TransactionManager transactionManager;
    private QueryParser queryParser = QueryParser.getInstance();

    public Transaction(Database db, TransactionManager transactionManager) {
        this.db = db;
        this.transactionManager = transactionManager;
    }

    private boolean checkOperation(String query, Object... params) {
        try {
            Query parsedQuery = queryParser.parse(query, Arrays.asList(params));
            if (parsedQuery == null) {
                return false;
            }

            switch (parsedQuery.getQueryType()) {
                case INSERT:
                    return true;
                case UPDATE:
                    return true;
                case DELETE:
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean execute(String query, Object... params) {
        if (!checkOperation(query, params)) {
            return false;
        }

        operations.add(new TransactionOperation(query, Arrays.asList(params)));
        return true;
    }

    public QueryResult commit() {
        for (TransactionOperation operation : operations) {
            QueryResult result = db.execute(operation.getQuery(), operation.getParams().toArray(new Object[0]));
            if (result.getQueryStatus() != QueryStatus.SUCCESS && result.getQueryStatus() != QueryStatus.NOT_FOUND) {
                rollback();
                return result;
            }
        }

        transactionManager.endTransaction();
        return new QueryResult(QueryStatus.SUCCESS);
    }

    public void rollback() {
        transactionManager.endTransaction();
    }
}