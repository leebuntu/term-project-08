package com.leebuntu.serialization;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteOutputProcessor {

    public static byte[] processOutput(OutputStreamProcessor processor) throws IOException, ClassCastException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            DataOutputStream dos = new DataOutputStream(baos);
            processor.process(dos);
            return baos.toByteArray();
        }
    }

    @FunctionalInterface
    public interface OutputStreamProcessor {
        void process(DataOutputStream dos) throws IOException;
    }
}
