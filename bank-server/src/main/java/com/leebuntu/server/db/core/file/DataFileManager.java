package com.leebuntu.server.db.core.file;

import com.leebuntu.server.db.core.CacheManager;
import com.leebuntu.server.db.query.Query;
import com.leebuntu.server.db.record.DBRecord;
import com.leebuntu.server.db.storage.Column;
import com.leebuntu.server.db.storage.Metadata;
import com.leebuntu.server.db.storage.Table;
import com.leebuntu.server.db.util.ConvertUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataFileManager {
	private RandomAccessFile dbFile;
	private File dbFileHandle;
	private CacheManager cacheManager;

	private Metadata metadata;
	private int metadataSize;

	private Object writeLock = new Object();
	private long fileEndPointer;
	private String rootPath;
	private String dbName;

	public DataFileManager(String rootPath, String dbName, CacheManager cacheManager, Metadata metadata,
			Integer metadataSize) throws IOException {
		this.rootPath = rootPath;
		this.dbName = dbName;
		this.cacheManager = cacheManager;
		this.metadata = metadata;
		this.metadataSize = metadataSize;

		String fullPathWithoutExt = rootPath + File.separator + dbName;
		this.dbFileHandle = new File(fullPathWithoutExt + ".db");

		if (!dbFileHandle.exists()) {
			throw new IOException("DB file not found");
		}

		this.dbFile = new RandomAccessFile(dbFileHandle, "rw");
		this.fileEndPointer = dbFile.length();
	}

	public List<LinkedHashMap<Column, byte[]>> read(Query query) throws IOException, ClassCastException {
		Table table = metadata.getTable(query.getTableName());
		int whereColumnIndex = query.getWhereColumnName() != null ? table.getColumnIndex(query.getWhereColumnName())
				: -1;
		byte[] whereKeyBytes = whereColumnIndex != -1
				? ConvertUtil.columnToBytes(query.getWhereKey(), table.getColumn(whereColumnIndex))
				: null;

		List<Integer> targetColumnIndices;
		if (query.getTargetColumnNames().get(0).equals("*")) {
			targetColumnIndices = new ArrayList<>();
			for (int i = 0; i < table.getColumns().size(); i++) {
				targetColumnIndices.add(i);
			}
		} else {
			targetColumnIndices = getTargetColumnIndices(table, query);
		}

		List<LinkedHashMap<Column, byte[]>> results = new ArrayList<>();

		try (FileChannel channel = new RandomAccessFile(dbFileHandle, "r").getChannel()) {
			MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, metadataSize,
					channel.size() - metadataSize);

			while (buffer.hasRemaining()) {
				if (table.isPrimaryKey(whereColumnIndex)) {
					if (!cacheManager.isExistPK(query.getTableName(), query.getWhereKey())) {
						return results;
					}

					Long offset = cacheManager.getPKOffset(query.getTableName(), query.getWhereKey());

					buffer.position(offset.intValue());

					int totalSize = buffer.getInt();
					byte[] data = new byte[totalSize];
					buffer.get(data);

					DBRecord record = new DBRecord();
					record.fromBytes(data);

					LinkedHashMap<Column, byte[]> row = new LinkedHashMap<>();
					for (int index : targetColumnIndices) {
						row.put(table.getColumn(index), record.getData(index));
					}
					results.add(row);

					break;
				} else {

					int totalSize = buffer.getInt();
					byte[] data = new byte[totalSize];
					buffer.get(data);

					DBRecord record = new DBRecord();
					record.fromBytes(data);

					if (!record.isDeletedRecord()
							&& recordMatches(record, whereColumnIndex, whereKeyBytes, query.getTableName())) {
						LinkedHashMap<Column, byte[]> row = new LinkedHashMap<>();
						for (int index : targetColumnIndices) {
							row.put(table.getColumn(index), record.getData(index));
						}
						results.add(row);
					}
				}
			}
		}

		return results;
	}

	public Object write(Query query) throws IOException, ClassCastException {
		synchronized (writeLock) {
			Table table = metadata.getTable(query.getTableName());
			List<Integer> targetColumnIndices = getTargetColumnIndices(table, query);

			Object pk = null;
			int i = 0;
			for (Integer index : targetColumnIndices) {
				if (table.isPrimaryKey(index)) {
					pk = query.getParameters().get(i);
					break;
				}
				i++;
			}

			if (pk == null) {
				while (true) {
					pk = getAutoIncrement(table, metadata.getTableAutoIncrementOffset(query.getTableName()));
					if (cacheManager.isExistPK(query.getTableName(), pk)) {
						continue;
					}
					break;
				}
				int pkIndex = table.getPKIndex();
				query.addParameter(pk);
				targetColumnIndices.add(pkIndex);
			}
			if (cacheManager.isExistPK(query.getTableName(), pk)) {
				return null;
			}

			DBRecord record = createRecord(query, table, targetColumnIndices);
			byte[] recordBytes = record.toBytes();

			dbFile.seek(fileEndPointer);
			dbFile.writeInt(recordBytes.length);
			dbFile.write(recordBytes);

			cacheManager.addPK(query.getTableName(), pk, fileEndPointer - metadataSize);
			fileEndPointer += recordBytes.length + 4;
			return pk;
		}
	}

	public boolean update(Query query) throws IOException, ClassCastException {
		synchronized (writeLock) {
			Table table = metadata.getTable(query.getTableName());
			int whereColumnIndex = table.getColumnIndex(query.getWhereColumnName());
			byte[] whereKeyBytes = ConvertUtil.columnToBytes(query.getWhereKey(), table.getColumn(whereColumnIndex));
			List<Integer> targetColumnIndices = getTargetColumnIndices(table, query);

			if (table.isPrimaryKey(whereColumnIndex)) {
				if (!cacheManager.isExistPK(query.getTableName(), query.getWhereKey())) {
					return false;
				}

				Long offset = cacheManager.getPKOffset(query.getTableName(), query.getWhereKey());
				offset += metadataSize;

				dbFile.seek(offset);
				int totalSize = dbFile.readInt();
				byte[] buffer = new byte[totalSize];
				dbFile.readFully(buffer);

				DBRecord record = new DBRecord();
				record.fromBytes(buffer);

				int tIndex = 0;
				for (int index : targetColumnIndices) {
					byte[] newData = ConvertUtil.columnToBytes(query.getParameters().get(tIndex),
							table.getColumn(index));
					record.updateData(index, newData);
					tIndex++;
				}

				dbFile.seek(offset);
				byte[] recordBytes = record.toBytes();
				dbFile.writeInt(recordBytes.length);
				dbFile.write(recordBytes);

				return true;
			} else {
				boolean updated = false;
				dbFile.seek(metadataSize);

				while (true) {
					try {
						long recordStart = dbFile.getFilePointer();
						int totalSize = dbFile.readInt();
						byte[] buffer = new byte[totalSize];
						dbFile.readFully(buffer);

						DBRecord record = new DBRecord();
						record.fromBytes(buffer);

						if (!record.isDeletedRecord() &&
								recordMatches(record, whereColumnIndex, whereKeyBytes, query.getTableName())) {
							int tIndex = 0;
							for (int index : targetColumnIndices) {
								byte[] newData = ConvertUtil.columnToBytes(query.getParameters().get(tIndex),
										table.getColumn(index));
								record.updateData(index, newData);
								tIndex++;
							}

							dbFile.seek(recordStart);
							byte[] recordBytes = record.toBytes();
							dbFile.writeInt(recordBytes.length);
							dbFile.write(recordBytes);

							updated = true;
							break;
						}
					} catch (EOFException e) {
						break;
					}
				}
				return updated;
			}
		}
	}

	public boolean delete(Query query) throws IOException, ClassCastException {
		synchronized (writeLock) {
			Table table = metadata.getTable(query.getTableName());
			String whereColumnName = query.getWhereColumnName();
			int whereColumnIndex = table.getColumnIndex(whereColumnName);
			byte[] whereKeyBytes = ConvertUtil.columnToBytes(query.getWhereKey(), table.getColumn(whereColumnIndex));

			if (table.isPrimaryKey(whereColumnIndex)) {
				Object pk = query.getWhereKey();
				if (!cacheManager.isExistPK(query.getTableName(), pk)) {
					return false;
				}

				long offset = cacheManager.getPKOffset(query.getTableName(), pk);
				offset += metadataSize;
				dbFile.seek(offset);

				int totalSize = dbFile.readInt();
				byte[] buffer = new byte[totalSize];
				dbFile.readFully(buffer);

				DBRecord record = new DBRecord();
				record.fromBytes(buffer);
				record.setDeleted(true);

				dbFile.seek(offset);
				dbFile.writeInt(record.toBytes().length);
				dbFile.write(record.toBytes());

				cacheManager.removePK(query.getTableName(), pk);
				return true;

			} else {
				boolean deleted = false;
				dbFile.seek(metadataSize);

				while (true) {
					try {
						long recordStart = dbFile.getFilePointer();
						int totalSize = dbFile.readInt();
						byte[] buffer = new byte[totalSize];
						dbFile.readFully(buffer);

						DBRecord record = new DBRecord();
						record.fromBytes(buffer);

						if (!record.isDeletedRecord() &&
								recordMatches(record, whereColumnIndex, whereKeyBytes, table.getTableName())) {
							record.setDeleted(true);
							dbFile.seek(recordStart);
							dbFile.writeInt(record.toBytes().length);
							dbFile.write(record.toBytes());
							deleted = true;
						}
					} catch (EOFException e) {
						break;
					}
				}

				return deleted;
			}
		}
	}

	public void compactEmptySpace() throws IOException {
		synchronized (writeLock) {
			File tmpFile = new File(rootPath + File.separator + dbName + ".tmp");
			try (RandomAccessFile tmpFileStream = new RandomAccessFile(tmpFile, "rw");
					RandomAccessFile dbFileStream = new RandomAccessFile(dbFileHandle, "rw");
					FileChannel sourceChannel = dbFileStream.getChannel();
					FileChannel tmpChannel = tmpFileStream.getChannel()) {

				sourceChannel.transferTo(0, metadataSize, tmpChannel);

				MappedByteBuffer buffer = sourceChannel.map(FileChannel.MapMode.READ_ONLY, metadataSize,
						sourceChannel.size() - metadataSize);
				while (buffer.hasRemaining()) {
					int totalSize = buffer.getInt();
					byte[] data = new byte[totalSize];
					buffer.get(data);

					DBRecord record = new DBRecord();
					record.fromBytes(data);

					if (!record.isDeletedRecord()) {
						ByteBuffer recordBuffer = ByteBuffer.allocate(Integer.BYTES + data.length);
						recordBuffer.putInt(totalSize);
						recordBuffer.put(data);
						recordBuffer.flip();

						tmpChannel.write(recordBuffer);
					}
				}
			}

			if (!dbFileHandle.delete()) {
				throw new IOException("Failed to delete db file");
			}
			if (!tmpFile.renameTo(dbFileHandle)) {
				throw new IOException("Failed to rename tmp file");
			}

			this.dbFile = new RandomAccessFile(dbFileHandle, "rw");
			this.fileEndPointer = dbFile.length();
		}
	}

	private int getAutoIncrement(Table table, int offset) throws IOException {
		dbFile.seek(offset);

		int autoIncrement = table.getAutoIncrement();
		dbFile.writeInt(autoIncrement + 1);

		return autoIncrement;
	}

	private boolean recordMatches(DBRecord record, int whereColumnIndex, byte[] whereKeyBytes, String tableName) {
		if (whereColumnIndex == -1) {
			return record.getTableName().equals(tableName);
		}
		return record.getTableName().equals(tableName) &&
				Arrays.equals(record.getData(whereColumnIndex), whereKeyBytes);
	}

	private List<Integer> getTargetColumnIndices(Table table, Query query) {
		List<Integer> targetColumnIndices = new ArrayList<>();
		for (String columnName : query.getTargetColumnNames()) {
			targetColumnIndices.add(table.getColumnIndex(columnName));
		}
		return targetColumnIndices;
	}

	private DBRecord createRecord(Query query, Table table, List<Integer> indexList) throws IOException {
		DBRecord record = new DBRecord();
		record.setTableName(query.getTableName());

		for (int i = 0; i < table.getColumns().size(); i++) {
			if (indexList.contains(i)) {
				int index = indexList.indexOf(i);
				byte[] data = ConvertUtil.columnToBytes(query.getParameters().get(index), table.getColumn(i));
				record.addData(data);
			} else {
				byte[] nullBytes = new byte[table.getColumn(i).getColumnSize()];
				record.addData(nullBytes);
			}
		}

		return record;
	}

	public Map<String, Map<Object, Long>> getIndexMap() throws IOException {
		Map<String, Map<Object, Long>> indexMap = new HashMap<>();
		dbFile.seek(metadataSize);

		for (Table table : metadata.getTables()) {
			indexMap.put(table.getTableName(), new HashMap<>());
		}

		while (true) {
			try {
				int totalSize = dbFile.readInt();
				byte[] buffer = new byte[totalSize];
				dbFile.readFully(buffer);

				DBRecord record = new DBRecord();
				record.fromBytes(buffer);

				String tableName = record.getTableName();

				Table table = metadata.getTable(tableName);
				int pkIndex = table.getPKIndex();

				if (record.isDeletedRecord()) {
					continue;
				}

				byte[] pk = record.getData(pkIndex);
				Column pkColumn = table.getColumn(pkIndex);
				Object pkObject = ConvertUtil.bytesToColumns(pk, pkColumn);

				indexMap.get(tableName).put(pkObject, dbFile.getFilePointer() - (totalSize + 4) - metadataSize);
			} catch (EOFException e) {
				break;
			}
		}

		return indexMap;
	}

	public void close() throws IOException {
		dbFile.close();
	}
}
