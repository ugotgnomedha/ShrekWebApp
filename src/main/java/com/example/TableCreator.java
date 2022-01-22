package com.example;

import com.example.DBConnect;
import com.example.ExcelParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Statement;

public class TableCreator {
    private static final Logger logger = LogManager.getLogger(TableCreator.class);
    public static void jcContactTable(){
        Statement statement;
        try {
            statement = DBConnect.connection.createStatement();
            statement.executeUpdate("CREATE TABLE jc_contact(id SERIAL UNIQUE, comment VARCHAR)");
            for (String header : ExcelParser.excelheaders) {
                if (header.contains("email") || header.contains("Email")) {
                    statement.executeUpdate("ALTER TABLE jc_contact ADD COLUMN email VARCHAR PRIMARY KEY");
                } else if (header.equals("")) {
                    continue;
                } else {
                    statement.executeUpdate("ALTER TABLE jc_contact ADD COLUMN " + header + " VARCHAR");
                }
            }
            statement.close();
        } catch (SQLException exception) {
            logger.error("Could not add column to a database table.");
        }
    }
}
