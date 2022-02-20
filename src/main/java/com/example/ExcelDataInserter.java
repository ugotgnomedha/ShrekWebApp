package com.example;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.sql.*;
import java.util.HashMap;

import static com.example.Constants.*;

public class ExcelDataInserter {
    private static final Logger logger = LogManager.getLogger(ExcelDataInserter.class);


    public static HashMap<String, Integer> domianFunc() {
        HashMap<String, Integer> domain_counter = new HashMap<>();
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT email FROM jc_contact");
            while (rs.next()) {
                if (rs.getString(1).contains("@")) {
                    String domain_full = rs.getString(1).substring(rs.getString(1).indexOf("@"));
                    domain_full = domain_full.replaceAll(" ", "");
                    String domain = domain_full.substring(domain_full.indexOf("."));
                    // get the value of the specified domain.
                    Integer count = domain_counter.get(domain);
                    if (domain_counter.containsKey(domain)) {
                        domain_counter.put(domain, count + 1);
                    } else {
                        domain_counter.put(domain, 1);
                    }
                }
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            logger.error("Invalid email format");
            e.printStackTrace();
        }
        return domain_counter;
    }

    public static void columnCheck(XSSFSheet sheet) {
        try {
            if (!ExcelParser.dbtableheaders.containsAll(ExcelParser.excelheaders)) {
                try {
                    Statement statement = DBConnect.connection.createStatement();
                    statement.executeUpdate("DROP TABLE " + "jc_contact" + "");  // Drop old table from database.
                    statement.close();
                } catch (SQLException ignored) {
                }
                TableCreator.jcContactTable(); //Create table and columns.
            }
            bulkInsertAssembler(sheet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bulkInsertAssembler(XSSFSheet sheet) {
        try {
            String insertSql = "INSERT INTO jc_contact" + "  (" + String.join(", ", ExcelParser.excelheaders) + ", comment) VALUES (-) ON CONFLICT (email) DO NOTHING";
            for (int i = 0; i < ExcelParser.excelheaders.size(); i++) {
                insertSql = insertSql.replaceAll("-", "?, -");
            }
            insertSql = insertSql.replace(", -", ", ?"); // Last one for comment.
            //System.out.println(insertSql);

            int column_count = sheet.getRow(0).getLastCellNum();
            int count = 1;

            PreparedStatement preparedStatement = DBConnect.connection.prepareStatement(insertSql);
            DBConnect.connection.setAutoCommit(false);

            for (Row row : sheet) {  // Switch rows.
                String comment = "-";
                for (int i = 0; i < column_count; i++) {  // Switch columns.
                    Cell cell = row.getCell(i);
                    String value = (cell == null) ? "-" : cell.toString();
                    if (value.contains("—")) {
                        comment = value.substring(value.indexOf("—") + 1);
                        value = value.substring(0, value.indexOf("—"));
                    } else if (value.contains("•")) {
                        comment = value.substring(value.indexOf("•") + 1);
                        value = value.substring(0, value.indexOf("•"));
                    }
                    preparedStatement.setString(count, value);
                    if (count == column_count) {
                        count = 0;
                    }
                    count++;
                }
                preparedStatement.setString(column_count + 1, comment);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            DBConnect.connection.commit();
            DBConnect.connection.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
