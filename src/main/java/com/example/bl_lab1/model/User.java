package com.example.bl_lab1.model;

import lombok.Data;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "user")
@XmlAccessorType (XmlAccessType.FIELD)
@Data
public class User {
    
    private Integer id;
    private String login;
    private String password;
    private String lastName;
    private String firstName;
    private String role;
    
}
