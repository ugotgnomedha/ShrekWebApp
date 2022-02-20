package com.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigGetter {
    public static void get_configs(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            Properties prop = new Properties();
            prop.load(fis);

            Constants.url = prop.getProperty("url");
            Constants.user = prop.getProperty("user");
            Constants.password = prop.getProperty("password");
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
