package com.example.bl_lab1.controllers;

import com.example.bl_lab1.security.JwtTokenUtil;
import com.example.bl_lab1.service.AuthenticationService;
import com.example.bl_lab1.utils.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import java.util.HashMap;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/user")
public class UserController {
    
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService authenticationService;
    private final JwtTokenUtil jwtTokenUtil;
    
    public UserController(AuthenticationManager authenticationManager, AuthenticationService authenticationService, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.authenticationService = authenticationService;
        this.jwtTokenUtil = jwtTokenUtil;
    }
    
    @RequestMapping(value = "/auth", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> auth(
          @RequestParam(value = "username") String username,
          @RequestParam(value = "password") String password) throws JsonProcessingException {
        HashMap<String, Object> response = new HashMap();
        System.out.println(new UsernamePasswordAuthenticationToken(username, password).getAuthorities());
        authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(username, password));
        CustomUserDetails foundUser = authenticationService.loadUserByUsername(username);
        response.put("token", jwtTokenUtil.generateToken(foundUser));
        //response.put("userId", foundUser.getAuthorities());
        return ResponseEntity.ok().body(response);
    }
}
