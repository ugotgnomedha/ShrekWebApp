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

    public static boolean connect() {
        boolean newTable = false;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException exception) {
            logger.error("Could not connect to database.");
            exception.printStackTrace();
        }
        if (connection != null) {
            newTable = ExcelParser.excelInitializer();
        }
        try {
            connection.close();
        } catch (SQLException exception) {
            logger.error("Could not close a connection to database.");
            exception.printStackTrace();
        }
        return newTable;
    }
}
