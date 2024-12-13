package com.leebuntu.communication.packet;

import com.leebuntu.serialization.ByteConvertible;
import com.leebuntu.serialization.ByteInputProcessor;
import com.leebuntu.serialization.ByteOutputProcessor;

import java.io.IOException;

public class Packet implements ByteConvertible {
    protected String path = "";
    protected String authToken = "";
    protected String jsonPayload = "";

    public String getPath() {
        return path;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getJsonPayload() {
        return jsonPayload;
    }

    public void setJsonPayload(String jsonPayload) {
        this.jsonPayload = jsonPayload;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public void fromBytes(byte[] payload) throws IOException, ArrayIndexOutOfBoundsException {
        ByteInputProcessor.processInput(payload, dis -> {
            this.path = dis.readUTF();
            this.jsonPayload = dis.readUTF();
            if (dis.available() > 0) {
                this.authToken = dis.readUTF();
            }
        });
    }

    @Override
    public byte[] toBytes() throws IOException {
        return ByteOutputProcessor.processOutput(dos -> {
            dos.writeUTF(path);
            dos.writeUTF(jsonPayload);
            if (authToken != null) {
                dos.writeUTF(authToken);
            }
        });
    }
}
