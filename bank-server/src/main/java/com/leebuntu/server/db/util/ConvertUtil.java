package com.leebuntu.server.db.util;

import com.leebuntu.server.db.storage.Column;
import com.leebuntu.common.serialization.ByteOutputProcessor;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class ConvertUtil {

    public static byte[] columnToBytes(Object value, Column column) throws IOException, ClassCastException {
        Class<?> type = column.getColumnType().getType();
        return ByteOutputProcessor.processOutput(dos -> {
            if (value == null) {
                byte[] nullBytes = new byte[column.getColumnSize()];
                dos.write(nullBytes);
            } else if (type.equals(Integer.class)) {
                dos.writeInt((Integer) value);
            } else if (type.equals(Long.class)) {
                dos.writeLong((Long) value);
            } else if (type.equals(Double.class)) {
                dos.writeDouble((Double) value);
            } else if (type.equals(String.class)) {
                int size = column.getColumnSize();
                byte[] fixedBytes = new byte[size];
                byte[] valueBytes = ((String) value).getBytes("UTF-8");

                System.arraycopy(valueBytes, 0, fixedBytes, 0, Math.min(valueBytes.length, size));
                dos.write(fixedBytes);
            }
        });

    }

    public static Object bytesToColumns(byte[] bytes, Column column) throws IOException, ClassCastException {
        Class<?> type = column.getColumnType().getType();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
            DataInputStream dis = new DataInputStream(bais);
            if (type.equals(Integer.class)) {
                return dis.readInt();
            } else if (type.equals(Long.class)) {
                return dis.readLong();
            } else if (type.equals(Double.class)) {
                return dis.readDouble();
            } else if (type.equals(String.class)) {
                if (Arrays.equals(bytes, new byte[bytes.length])) {
                    return null;
                }
                int size = column.getColumnSize();
                byte[] buffer = new byte[size];
                dis.readFully(buffer);

                int nullIndex = 0;
                while (nullIndex < buffer.length && buffer[nullIndex] != 0x00) {
                    nullIndex++;
                }

                byte[] result = new byte[nullIndex];
                System.arraycopy(buffer, 0, result, 0, nullIndex);

                return new String(result, "UTF-8");
            }

            return null;
        }
    }
}
