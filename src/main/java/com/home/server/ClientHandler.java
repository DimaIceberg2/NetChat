package com.home.server;

import org.hibernate.Session;
import redis.clients.jedis.Jedis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class ClientHandler {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Jedis jedis;
    private User user;
    private String listNameJedis;

    public ClientHandler(Server server, Socket socket, Jedis jedis, String listNameJedis) {
        try {
            this.listNameJedis = listNameJedis;
            this.jedis = jedis;
            this.socket = socket;
            dataInputStream = new DataInputStream(this.socket.getInputStream());
            dataOutputStream = new DataOutputStream(this.socket.getOutputStream());

            String name = dataInputStream.readUTF();
            if (name.equals("/exit")) return;
            user = new User(name);
            System.out.println(name + " is connected");
            dataOutputStream.writeUTF("Hello, " + user.getName() + "! NetChat have " + (server.getClients().size() + 1) + " users!");
            dataOutputStream.writeUTF("Print available commands /help");

            Thread thread = new Thread(() -> {
                while(true) {
                    try {
                        String message = dataInputStream.readUTF();
                        if (message.equals("/exit")) {
                            System.out.println(user.getName() + " is disconnected");
                            server.broadcast(user.getName() + " is disconnected");
                            server.getClients().remove(this);
                            break;
                        } else if(message.equals("/userlist")) {
                            dataOutputStream.writeUTF("Active users:");
                            for (ClientHandler i : server.getClients()) {
                                dataOutputStream.writeUTF(i.user.getName());
                            }
                        } else if (message.matches("/showhistory .*")) {
                            dataOutputStream.writeUTF("History:");
                            int countRows = Integer.parseInt(message.replaceAll("\\D+",""));
                            showHistory(countRows);

                        } else {
                            Date date = new Date();
                            server.broadcast(message);
                            addMessageToDB(message, date);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void addMessageToDB(String message, Date date) {
        jedis.rpush(listNameJedis, date + " " + user.getName() + ": " + message);
    }

    void showMessage(String message) {
        try {
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void showHistory(int countRows) {
        try {
            if (jedis.llen(listNameJedis) < countRows) countRows = (int)jedis.llen(listNameJedis);
            List<String> list = jedis.lrange(listNameJedis, jedis.llen(listNameJedis) - countRows, jedis.llen(listNameJedis));
            for (String i : list) {
                dataOutputStream.writeUTF(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
