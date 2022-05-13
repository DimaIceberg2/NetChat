package com.home.server.entitiys;

import com.home.server.entitiys.Message;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    List<Message> messageList;

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public List<Message> getMessageList() {return messageList;}
    public void setMessageList(List<Message> messageList) {this.messageList = messageList;}

    public User() {}
    public User(String name) {this.name = name;}
}
