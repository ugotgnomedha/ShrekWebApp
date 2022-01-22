package com.example;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.example.Constants.*;

public class DBConnect {
    private static final Logger logger = LogManager.getLogger(DBConnect.class);
    public static Connection connection = null;
    public static void connect() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException exception) {
            logger.error("Could not connect to database.");
        }
        if (connection != null){
            ExcelParser.excelInitializer(); // Start parsing excel file.
        }
        try {
            connection.close();
        }catch (SQLException exception){
            logger.error("Could not close a connection to database.");
        }
    }
}
