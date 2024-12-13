package com.leebuntu.communication;

import com.leebuntu.communication.dto.Payload;
import com.leebuntu.communication.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connector {
    private Socket socket;

    private InputStream is;
    private DataInputStream dis;

    private OutputStream os;
    private DataOutputStream dos;

    public Connector(String host, int port) throws IOException {
        socket = new Socket(host, port);
        is = socket.getInputStream();
        dis = new DataInputStream(is);
        os = socket.getOutputStream();
        dos = new DataOutputStream(os);
    }

    public void send(String path, String authToken, Payload<?> payload) throws IOException {
        Packet packet = new Packet();
        packet.setPath(path);
        packet.setAuthToken(authToken);
        if (payload != null) {
            packet.setJsonPayload(payload.toJson());
            System.out.println("Sending payload: " + payload.toJson());
        }
        byte[] buffer = packet.toBytes();
        dos.writeInt(buffer.length);
        dos.write(buffer);
        dos.flush();
    }

    public boolean receiveAndBind(Payload<?> bindTarget) throws IOException {
        while (true) {
            if (dis.available() > 4) {
                int packetSize = dis.readInt();

                byte[] buffer = new byte[packetSize];
                int bytesRead = 0;

                while (bytesRead < packetSize) {
                    int read = is.read(buffer, bytesRead, packetSize - bytesRead);
                    if (read == -1) {
                        return false;
                    }
                    bytesRead += read;
                }

                Packet packet = new Packet();
                packet.fromBytes(buffer);
                System.out.println(packet.getJsonPayload());

                bindTarget.fromJson(packet.getJsonPayload());
                return true;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    return false;
                }
            }
        }
    }
}
