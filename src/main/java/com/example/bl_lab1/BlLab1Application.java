package com.example.bl_lab1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
public class BlLab1Application {

    public static void main(String[] args) {
        SpringApplication.run(BlLab1Application.class, args);
        System.out.println("SWAGGER: http://localhost:8080/api/swagger-ui/index.html#/");
    
    }

}
