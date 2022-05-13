package com.home.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Server {
    private static ServerSocket serverSocket;
    private static final int port = 8189;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static List<ClientHandler> getClients() {
        return clients;
    }

    public void serverStart() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    clients.add(new ClientHandler(this, socket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server is down");
    }

    public void broadcast(String message) {
        for(ClientHandler i : clients){
            i.showMessage(message);
        }
    }
}
