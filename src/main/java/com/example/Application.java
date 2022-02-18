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
//        final String dir = System.getProperty("user.dir");
//        String configPath = dir + "\\ShrekAppConfig.properties";
//        logger.info("config file - " + configPath + "");
//        ConfigGetter.get_configs(configPath);
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

