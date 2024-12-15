package com.leebuntu.common.communication.router;

import com.leebuntu.common.communication.dto.Response;
import com.leebuntu.common.communication.dto.enums.Status;
import com.leebuntu.common.communication.packet.Packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Router {
    private HashMap<String, ContextHandler> routerMap;
    private HashMap<String, List<Middleware>> routerMiddlewareMap;
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private int activeConnections = 0;

    public Router(int port) throws IOException {
        routerMap = new HashMap<>();
        routerMiddlewareMap = new HashMap<>();
        serverSocket = new ServerSocket(port);
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
                        for (Middleware middleware : middlewares) {
                            if (!middleware.process(context)) {
                                continue top;
                            }
                        }

                        ContextHandler handler = routerMap.get(packet.getPath());
                        if (handler != null) {
                            handler.handle(context);
                        } else {
                            context.reply(new Response(Status.FAILED, "Invalid route"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break mid;
                    } catch (Exception e) {
                        e.printStackTrace();
                        context.reply(new Response(Status.INTERNAL_ERROR, "Internal server error"));
                    }
                }
                activeConnections--;
                System.out.println("Connection closed");
                break;
            }
        } catch (IOException e) {
            activeConnections--;
            System.out.println("Connection closed");
        }

    }

    public void start() {
        isRunning = true;
        new Thread(() -> {
            while (isRunning) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> handle(socket)).start();
                    System.out.println("New connection");
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
