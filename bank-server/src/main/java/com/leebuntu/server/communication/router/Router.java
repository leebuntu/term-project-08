package com.leebuntu.server.communication.router;

import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.common.communication.packet.Packet;
import com.leebuntu.server.Server;
import com.leebuntu.server.Server.LogType;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Router {
    private HashMap<String, ContextHandler> routerMap;
    private HashMap<String, List<Middleware>> routerMiddlewareMap;
    private ServerSocket serverSocket;
    private List<Socket> sockets;
    private boolean isRunning = false;
    private int activeConnections = 0;

    public Router(int port) throws IOException {
        routerMap = new HashMap<>();
        routerMiddlewareMap = new HashMap<>();
        serverSocket = new ServerSocket(port);
        sockets = new ArrayList<>();
    }

    public void addRoute(String route, ContextHandler handler, Middleware... middlewares) {
        routerMap.put(route, handler);
        routerMiddlewareMap.put(route, new ArrayList<>());
        for (Middleware middleware : middlewares) {
            routerMiddlewareMap.get(route).add(middleware);
        }
    }

    public void stop() {
        isRunning = false;
        activeConnections = 0;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Socket socket : sockets) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getIp(Socket socket) {
        InetAddress address = socket.getInetAddress();
        return address.getHostAddress();
    }

    private void handle(Socket socket) {
        try (InputStream is = socket.getInputStream();
                DataInputStream dis = new DataInputStream(is)) {
            top: while (isRunning) {
                mid: while (!socket.isClosed()) {
                    int packetSize = dis.readInt();

                    byte[] buffer = new byte[packetSize];
                    int bytesRead = 0;

                    while (bytesRead < packetSize) {
                        int read = is.read(buffer, bytesRead, packetSize - bytesRead);
                        if (read == -1) {
                            return;
                        }
                        bytesRead += read;
                    }

                    Packet packet = new Packet();
                    packet.fromBytes(buffer);

                    System.out.println("Received json: " + packet.getJsonPayload());

                    Context context = new Context(socket, packet);

                    try {
                        List<Middleware> middlewares = routerMiddlewareMap.get(packet.getPath());
                        if (middlewares != null) {
                            for (Middleware middleware : middlewares) {
                                if (!middleware.process(context)) {
                                    continue top;
                                }
                            }
                        }

                        ContextHandler handler = routerMap.get(packet.getPath());
                        if (handler != null) {
                            handler.handle(context);
                        } else {
                            context.reply(new Response(Status.FAILED, "Invalid route"));
                        }
                    } catch (IOException e) {
                        break mid;
                    } catch (Exception e) {
                        context.reply(new Response(Status.INTERNAL_ERROR, "Internal server error"));
                        Server.getInstance().printToLog(LogType.ERROR,
                                "Internal server error:\n" + e.getMessage() + "\n");
                    }
                }
                activeConnections--;
                Server.getInstance().printToLog(LogType.INFO, "Connection closed from: " + getIp(socket));
                break;
            }

            Server.getInstance().printToLog(LogType.INFO, "Connection closed from: " + getIp(socket));
            socket.close();

        } catch (IOException e) {
            activeConnections--;
            Server.getInstance().printToLog(LogType.INFO, "Connection closed from: " + getIp(socket));
        }

    }

    public void start() {
        isRunning = true;
        new Thread(() -> {
            while (isRunning) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> handle(socket)).start();
                    Server.getInstance().printToLog(LogType.INFO, "New connection from: " + getIp(socket));
                    sockets.add(socket);
                    activeConnections++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public int getActiveConnections() {
        return activeConnections;
    }
}
