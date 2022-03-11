package com.example;

import com.example.storage.StorageService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.File;

import static com.example.Constants.uploadDirectory;

@SpringBootApplication
@EnableConfigurationProperties(com.example.storage.StorageProperties.class)
public class Application {

    private static final Logger logger = LogManager.getLogger(Application.class);
    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        try {
            new File(uploadDirectory).mkdir();
            SpringApplication.run(Application.class, args);
            ShrekBD.tempBeginConnection();  // Start 1 connection at the program launch. Must change later with multi-user update.
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }

}

