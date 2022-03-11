package com.example;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Statement;

public class TableCreator {
    private static final Logger logger = LogManager.getLogger(TableCreator.class);

    public static void jcContactTable() {
        Statement statement;
        try {
            statement = DBConnect.connection.createStatement();
            statement.executeUpdate("CREATE TABLE jc_contact(id SERIAL UNIQUE, comment VARCHAR)");
            for (String header : ExcelParser.excelheaders) {
                if (header.contains("email") || header.contains("Email") || header.contains("почта")
                        || header.contains("Почта") || header.contains("e-mail")) {
                    statement.executeUpdate("ALTER TABLE jc_contact ADD COLUMN " + ExcelParser.emailNameFromExcel + " VARCHAR PRIMARY KEY");
                } else if (header.equals("")) {
                    continue;
                } else {
                    statement.executeUpdate("ALTER TABLE jc_contact ADD COLUMN " + header + " VARCHAR");
                }
            }
            statement.close();
        } catch (Exception exception) {
            logger.error("Could not add column to a database table.");
            exception.printStackTrace();
        }
    }
}
