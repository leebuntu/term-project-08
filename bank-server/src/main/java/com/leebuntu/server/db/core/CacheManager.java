package com.leebuntu.server.db.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CacheManager {

	private HashMap<String, HashMap<Object, Long>> pkOffsetMap;
	private HashMap<String, HashSet<Object>> pkCache;

	public CacheManager() {
		this.pkOffsetMap = new HashMap<>();
		this.pkCache = new HashMap<>();
	}

	public void initPKCache(String tableName, Map<Object, Long> pkOffsetMap) {
		this.pkOffsetMap.put(tableName, new HashMap<>(pkOffsetMap));
		this.pkCache.put(tableName, new HashSet<>(pkOffsetMap.keySet()));
	}

	public boolean isExistPK(String tableName, Object pk) {
		return pkCache.get(tableName).contains(pk);
	}

	public void addPK(String tableName, Object pk, long offset) {
		pkCache.get(tableName).add(pk);
		pkOffsetMap.get(tableName).put(pk, offset);
	}

	public void removePK(String tableName, Object pk) {
		pkCache.get(tableName).remove(pk);
		pkOffsetMap.get(tableName).remove(pk);
	}

	public long getPKOffset(String tableName, Object pk) {
		return pkOffsetMap.get(tableName).get(pk);
	}
}
