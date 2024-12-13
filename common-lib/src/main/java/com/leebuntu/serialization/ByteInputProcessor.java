package com.leebuntu.serialization;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ByteInputProcessor {

    public static void processInput(byte[] payload, InputStreamProcessor processor) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(payload);
                DataInputStream dis = new DataInputStream(bais)) {
            processor.process(dis);
        }
    }

    @FunctionalInterface
    public interface InputStreamProcessor {
        void process(DataInputStream dis) throws IOException;
    }
}
