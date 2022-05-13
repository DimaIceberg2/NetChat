package com.home.server;

import com.home.server.entitiys.Message;
import com.home.server.entitiys.User;
import org.hibernate.Session;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ClientHandler {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private User user;

    public User getUser() {
        return user;
    }

    private Message message;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.socket = socket;
            dataInputStream = new DataInputStream(this.socket.getInputStream());
            dataOutputStream = new DataOutputStream(this.socket.getOutputStream());

            String name = dataInputStream.readUTF();
            if (name.equals("/exit")) return;
            user = new User(name);
            addUserToDB();
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
                            removeUserFromDB();
                            server.getClients().remove(this);
                            break;
                        } else if(message.equals("/userlist")) {
                            dataOutputStream.writeUTF("Active users:");
                            for (ClientHandler i : server.getClients()) {
                                dataOutputStream.writeUTF(i.getUser().getName());
                            }
                        } else {
                            server.broadcast(user.getName() + ": " + message);
                            Date date = new Date();
                            addMessageToDB(new Message(date.toString(), message, user));
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

    void addUserToDB() {
        Session session = MainApp.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }

    void addMessageToDB(Message message) {
        Session session = MainApp.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.save(message);
        session.getTransaction().commit();
        session.close();
    }

    void removeUserFromDB(){
        Session session = MainApp.sessionFactory.getCurrentSession();
        session.beginTransaction();
        session.remove(user);
        session.getTransaction().commit();
        session.close();
    }

    void showMessage(String message) {
        try {
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
