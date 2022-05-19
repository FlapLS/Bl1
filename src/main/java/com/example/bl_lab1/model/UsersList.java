package com.example.bl_lab1.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "UsersList")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsersList {
    
    @XmlElement(name = "user")
    List<User> users;
    
    
    public List<User> getUsers() {
        return users;
    }
    
    public void setUsers(List<User> users) {
        this.users = users;
    }
}
