package com.leebuntu.server.db.core;

import com.leebuntu.server.db.core.file.DataFileManager;
import com.leebuntu.server.db.core.transaction.TransactionManager;
import com.leebuntu.server.db.query.Query;
import com.leebuntu.server.db.query.QueryResult;
import com.leebuntu.server.db.query.enums.QueryStatus;
import com.leebuntu.server.db.storage.Column;

import java.util.LinkedHashMap;
import java.util.List;

public class DataManager {
	private DataFileManager fileManager;
	private TransactionManager transactionManager;

	public DataManager(DataFileManager fileManager, TransactionManager transactionManager) {
		this.fileManager = fileManager;
		this.transactionManager = transactionManager;
	}

	public QueryResult select(Query query) {
		transactionManager.waitForTransaction();
		try {
			List<LinkedHashMap<Column, byte[]>> result = fileManager.read(query);
			QueryResult queryResult = new QueryResult();
			if (result.isEmpty()) {
				return new QueryResult(QueryStatus.NOT_FOUND);
			}
			queryResult.setResult(result);
			queryResult.setQueryStatus(QueryStatus.SUCCESS);
			return queryResult;
		} catch (Exception e) {
			e.printStackTrace();
			return new QueryResult(QueryStatus.FAILED);
		}
	}

	public QueryResult insert(Query query) {
		transactionManager.waitForTransaction();
		try {
			Object lastInsertId = fileManager.write(query);
			if (lastInsertId != null) {
				return new QueryResult(QueryStatus.SUCCESS, lastInsertId);
			} else {
				return new QueryResult(QueryStatus.DUPLICATED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new QueryResult(QueryStatus.FAILED);
		}
	}

	public QueryResult update(Query query) {
		transactionManager.waitForTransaction();
		try {
			if (fileManager.update(query)) {
				return new QueryResult(QueryStatus.SUCCESS);
			} else {
				return new QueryResult(QueryStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new QueryResult(QueryStatus.FAILED);
		}
	}

	public QueryResult delete(Query query) {
		transactionManager.waitForTransaction();
		try {
			if (fileManager.delete(query)) {
				return new QueryResult(QueryStatus.SUCCESS);
			} else {
				return new QueryResult(QueryStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new QueryResult(QueryStatus.FAILED);
		}
	}

}
