package com.leebuntu.common.serialization;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ByteChainInputProcessor {

    public static DataInputStream processInput(byte[] payload, ChainInputStreamProcessor processor) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        DataInputStream dis = new DataInputStream(bais);
        return processor.process(dis);
    }

    @FunctionalInterface
    public interface ChainInputStreamProcessor {
        DataInputStream process(DataInputStream dis) throws IOException;
    }

}
