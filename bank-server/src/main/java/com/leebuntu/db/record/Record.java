package com.leebuntu.db.record;

import com.leebuntu.serialization.ByteConvertible;

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
