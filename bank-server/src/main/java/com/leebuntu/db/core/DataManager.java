package com.leebuntu.db.core;

import com.leebuntu.db.core.file.DataFileManager;
import com.leebuntu.db.query.Query;
import com.leebuntu.db.query.QueryResult;
import com.leebuntu.db.query.enums.QueryStatus;
import com.leebuntu.db.storage.Column;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class DataManager {
	private DataFileManager fileManager;

	private final ReentrantLock lock = new ReentrantLock();

	public DataManager(DataFileManager fileManager) {
		this.fileManager = fileManager;
	}

	public QueryResult select(Query query) {
		try {
			List<LinkedHashMap<Column, byte[]>> result = fileManager.read(query);
			QueryResult queryResult = new QueryResult();
			if (result.isEmpty()) {
				return new QueryResult(QueryStatus.NOT_FOUND);
			}
			queryResult.setResult(result);
			queryResult.setQueryStatus(QueryStatus.SUCCESS);
			return queryResult;
		} catch (IOException e) {
			return new QueryResult(QueryStatus.FAILED);
		} catch (ClassCastException e) {
			return new QueryResult(QueryStatus.FAILED);
		}
	}

	public QueryResult insert(Query query) {
		try {
			Object lastInsertId = fileManager.write(query);
			if (lastInsertId != null) {
				return new QueryResult(QueryStatus.SUCCESS, lastInsertId);
			} else {
				return new QueryResult(QueryStatus.DUPLICATED);
			}
		} catch (IOException e) {
			return new QueryResult(QueryStatus.FAILED);
		} catch (ClassCastException e) {
			return new QueryResult(QueryStatus.FAILED);
		}
	}

	public QueryResult update(Query query) {
		try {
			if (fileManager.update(query)) {
				return new QueryResult(QueryStatus.SUCCESS);
			} else {
				return new QueryResult(QueryStatus.NOT_FOUND);
			}
		} catch (IOException e) {
			return new QueryResult(QueryStatus.FAILED);
		} catch (ClassCastException e) {
			return new QueryResult(QueryStatus.FAILED);
		}
	}

	public QueryResult delete(Query query) {
		try {
			if (fileManager.delete(query)) {
				return new QueryResult(QueryStatus.SUCCESS);
			} else {
				return new QueryResult(QueryStatus.NOT_FOUND);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new QueryResult(QueryStatus.FAILED);
		} catch (ClassCastException e) {
			e.printStackTrace();
			return new QueryResult(QueryStatus.FAILED);
		}
	}

}
