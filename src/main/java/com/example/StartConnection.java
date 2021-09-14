package com.example;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.example.Constants.*;

public class StartConnection {
    private static final Logger logger = LogManager.getLogger(StartConnection.class);
    public static Connection connect;
    public static Statement stat;

    public static void start() {
        try {
            Connection con = DriverManager.getConnection("jdbc:postgresql://localhost/postgres", user, password);
            connect = con;
            Statement st = con.createStatement();
            stat = st;
            if (con != null) {
//                ExelParser.parseExel();
                ShrekBD shrek = new ShrekBD();
                parser_excel.parser(new File(uploadDirectory + "\\" + shrek.listFilesUsingDirectoryStream(uploadDirectory).get(0)));
            } else {
                System.out.println("No connection to database!");
            }
        } catch (SQLException | IOException loser_exception) {
           logger.error("Connection failed", loser_exception);
        }
    }


}

