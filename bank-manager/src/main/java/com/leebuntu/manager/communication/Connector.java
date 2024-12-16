package com.leebuntu.manager.communication;

import com.leebuntu.common.communication.dto.Payload;
import com.leebuntu.common.communication.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connector {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private final String host;
    private final int port;

    public Connector(String host, int port) {
        this.host = host;
        this.port = port;
        connect();
    }

    private boolean connect() {
        try {
            socket = new Socket(host, port);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected to server at " + host + ":" + port);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            return false;
        }
    }

    public boolean send(String path, String authToken, Payload<?> payload) {
        if (socket == null) {
            if (!connect())
                return false;
        }

        Packet packet = new Packet();
        packet.setPath(path);
        packet.setAuthToken(authToken);
        if (payload != null) {
            packet.setJsonPayload(payload.toJson());
        }

        try {
            byte[] buffer = packet.toBytes();
            dos.writeInt(buffer.length);
            dos.write(buffer);
            dos.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to send data: " + e.getMessage());
            socket = null;
            return false;
        }
    }

    public boolean receiveAndBind(Payload<?> bindTarget) {
        try {
            int packetSize = dis.readInt();
            byte[] buffer = new byte[packetSize];
            dis.readFully(buffer);

            Packet packet = new Packet();
            packet.fromBytes(buffer);
            bindTarget.fromJson(packet.getJsonPayload());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to receive data: " + e.getMessage());
            socket = null;
            return false;
        }
    }
}