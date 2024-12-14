package com.leebuntu.server.db.record;

import com.leebuntu.common.serialization.ByteConvertible;

public abstract class Record implements ByteConvertible {
	protected String tableName;

	public Record(String tableName) {
		this.tableName = tableName;
	}

	public Record() {
		this("");
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
