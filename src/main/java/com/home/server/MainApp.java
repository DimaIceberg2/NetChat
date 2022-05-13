package com.home.server;

import com.home.server.entitiys.Message;
import com.home.server.entitiys.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class MainApp{
    static SessionFactory sessionFactory;

    public static void main(String[] args) {
        sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Message.class)
                .buildSessionFactory();

        Server server = new Server();
        server.serverStart();
    }
}
