package com.leebuntu.server.communication.router;

import com.leebuntu.common.communication.dto.Payload;
import com.leebuntu.common.communication.packet.Packet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Context {
    private Socket socket;
    private OutputStream oos;
    private DataOutputStream dos;
    private Packet receivedPacket;
    private Map<String, Object> field;

    public Context(Socket socket, Packet receivedPacket) throws IOException {
        this.socket = socket;
        this.oos = socket.getOutputStream();
        this.dos = new DataOutputStream(oos);
        this.receivedPacket = receivedPacket;
        this.field = new HashMap<>();
    }

    public boolean bind(Payload<?> bindTarget) {
        try {
            bindTarget.fromJson(receivedPacket.getJsonPayload());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void setField(String key, Object value) {
        field.put(key, value);
    }

    public Object getField(String key) {
        return field.get(key);
    }

    public String getAuthToken() {
        return receivedPacket.getAuthToken();
    }

    public String getJsonPayload() {
        return receivedPacket.getJsonPayload();
    }

    public void reply(Payload<?> payload) {
        try {
            Packet replyPacket = new Packet();
            replyPacket.setJsonPayload(payload.toJson());
            System.out.println("Sending reply: " + payload.toJson());
            byte[] buffer = replyPacket.toBytes();
            dos.writeInt(buffer.length);
            dos.write(buffer);
            dos.flush();
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException e1) {
            }
        }
    }
}
