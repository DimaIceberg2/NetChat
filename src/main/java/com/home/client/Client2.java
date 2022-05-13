package com.home.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {
    private static Socket socket;
    private final static int port = 8189;
    private static String message = new String();
    private static String userName;

    public static void main(String[] args) {
        try {
            System.out.println("Please input your name...");
            Scanner scanner = new Scanner(System.in);
            userName = scanner.nextLine();
            socket = new Socket("localhost", port);

            Thread threadRead = new Thread(() -> {
                read();
            });

            Thread threadWrite = new Thread(() -> {
                write();
            });

            threadRead.start();
            threadWrite.start();

            try {
                //threadRead.join();
                threadWrite.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void read() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            while (true) {
                String chatMessage = dataInputStream.readUTF();
                System.out.println(chatMessage);
            }
        }catch (IOException e) {
            System.out.println("Connection closed");
        }
    }

    private static void write() {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            message = userName;
            if (message.equals("/exit")) return;

            dataOutputStream.writeUTF(message);
            while (true) {
                message = scanner.nextLine();
                switch(message){
                    case "/exit": {
                        dataOutputStream.writeUTF(message);
                        return;
                    }
                    case "/help": {
                        System.out.println("For exit input /exit");
                        System.out.println("For help input /help");
                        System.out.println("Print all users input /userlist");
                        System.out.println("Show history message input /showhistory N");
                        break;
                    }
                    default: {
                        dataOutputStream.writeUTF(message);
                        break;
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
