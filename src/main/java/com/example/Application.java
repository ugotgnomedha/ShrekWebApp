package com.example;

import com.example.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.File;

import static com.example.Constants.uploadDirectory;

@SpringBootApplication
@EnableConfigurationProperties(com.example.storage.StorageProperties.class)
public class Application {

    public static void main(String[] args) {
        new File(uploadDirectory).mkdir();
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }

}

