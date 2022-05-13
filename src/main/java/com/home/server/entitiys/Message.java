package com.home.server.entitiys;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String time;

    @Column
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public Message() {}
    public Message(String time, String message, User user) {
        this.time = time;
        this.message = message;
        this.user = user;
    }
}
