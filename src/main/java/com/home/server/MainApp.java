package com.home.server;

import org.hibernate.SessionFactory;
import redis.clients.jedis.Jedis;

public class MainApp{
    static SessionFactory sessionFactory;

    public static void main(String[] args) {
        Server server = new Server();
        server.serverStart();


//        Jedis jedis = new Jedis("localhost", 6379);
//
//        jedis.rpush("a", "one");
//        jedis.rpush("a", "two");
//        jedis.rpush("a", "three");
//        System.out.println(jedis.lrange("a", 0, 19));
    }
}
