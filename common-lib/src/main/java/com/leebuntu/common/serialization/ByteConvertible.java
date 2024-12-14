package com.leebuntu.common.serialization;

import java.io.IOException;

public interface ByteConvertible {
    byte[] toBytes() throws IOException;

    void fromBytes(byte[] bytes) throws IOException, ArrayIndexOutOfBoundsException;
}
