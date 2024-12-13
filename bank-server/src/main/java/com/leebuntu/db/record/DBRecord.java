package com.leebuntu.db.record;

import com.leebuntu.serialization.ByteInputProcessor;
import com.leebuntu.serialization.ByteOutputProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBRecord extends Record {
	private boolean isDeleted;
	private List<byte[]> dataz;

	public DBRecord(String tableName, List<byte[]> dataz) {
		super(tableName);
		this.dataz = dataz;
	}

	public DBRecord() {
		this.isDeleted = false;
		this.dataz = new ArrayList<>();
	}

	public List<byte[]> getDataz() {
		return this.dataz;
	}

	public void setDataz(List<byte[]> dataz) {
		this.dataz = dataz;
	}

	public byte[] getData(int index) {
		return this.dataz.get(index);
	}

	public void addData(byte[] data) {
		this.dataz.add(data);
	}

	public void updateData(int index, byte[] data) {
		this.dataz.set(index, data);
	}

	/**
	 * 바이트 배열을 읽어 내부 필드에 저장
	 * [is_deleted(1)][table_name(n)][data_size(4)][data(n)]...
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	@Override
	public void fromBytes(byte[] bytes) throws IOException, ArrayIndexOutOfBoundsException {
		ByteInputProcessor.processInput(bytes, (dis) -> {
			this.isDeleted = dis.readBoolean();
			super.tableName = dis.readUTF();
			while (dis.available() > 0) {
				int dataSize = dis.readInt();
				byte[] data = new byte[dataSize];
				dis.readFully(data);
				this.dataz.add(data);
			}
		});
	}

	/**
	 * 바이트 배열로 변환
	 * [is_deleted(1)][table_name(n)][data_size(4)][data(n)]...
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public byte[] toBytes() throws IOException {
		return ByteOutputProcessor.processOutput((dos) -> {
			dos.writeBoolean(this.isDeleted);
			dos.writeUTF(super.tableName);
			for (byte[] data : this.dataz) {
				dos.writeInt(data.length);
				dos.write(data);
			}
		});
	}

	public boolean isDeletedRecord() {
		return this.isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
}
