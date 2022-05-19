package com.example.bl_lab1.service;

import com.example.bl_lab1.model.User;
import com.example.bl_lab1.model.UsersList;
import com.example.bl_lab1.utils.CustomUserDetails;
import lombok.SneakyThrows;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthenticationService
      implements UserDetailsService {
    
    private static final String PATH_TO_XML = "users.xml";
    
    public User getUser(String login) throws JAXBException {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        return null;
    }
    
    private List<User> getAllUsers() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(UsersList.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        
        //We had written this file in marshalling example
        UsersList usersList = (UsersList) jaxbUnmarshaller.unmarshal( new File(PATH_TO_XML));
        return usersList.getUsers();
    }
    
    @SneakyThrows
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       
        User user = getUser(username);
    
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new CustomUserDetails(user.getId(), username, user.getPassword(), authorities);
    }
}
