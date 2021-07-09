package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        new File(ApplicationController.uploadDirectory).mkdir();
        SpringApplication.run(Application.class, args);
    }
}