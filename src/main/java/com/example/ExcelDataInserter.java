package com.example;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
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
        }
        return domain_counter;
    }

    public static void inserter(XSSFSheet sheet) {
        try {
            Statement statement = DBConnect.connection.createStatement();
            int column_count = sheet.getRow(0).getLastCellNum();
            for (int i = 0; i < column_count; i++) {  // Switch columns.
                for (Row row : sheet) {  // Switch rows.
                    if (ExcelParser.excelheaders.get(i).equals("Email") || ExcelParser.excelheaders.get(i).equals("email")) {
                        Cell cell_email = row.getCell(ExcelParser.emailColumnIndex);
                        statement.executeUpdate("INSERT INTO " + "jc_contact" + "(email) VALUES ('" + cell_email + "') ON CONFLICT (email) DO NOTHING");
                    } else {
                        Cell cell_ = row.getCell(i);
                        if (cell_ != null) {
                            String cell = cell_.toString();
                            String comment = "-";
                            String value = (cell == null) ? "-" : cell;
                            if (value.contains("—")) {
                                comment = value.substring(value.indexOf("—") + 1);
                            } else if (value.contains("•")) {
                                comment = value.substring(value.indexOf("•") + 1);
                            }

                            if (cell.contains("•")) {
                                cell = cell.substring(0, cell.indexOf("•"));
                            } else if (cell.contains("—")) {
                                cell = cell.substring(0, cell.indexOf("—"));
                            }
                            statement.executeUpdate("INSERT INTO jc_contact(email, " + ExcelParser.excelheaders.get(i) + ", comment) VALUES ('" + row.getCell(ExcelParser.emailColumnIndex) + "', '" + cell + "', '" + comment + "') ON CONFLICT (email) DO UPDATE SET " + ExcelParser.excelheaders.get(i) + " = '" + cell + "'");
                            if (!comment.equals("-")) {
                                statement.executeUpdate("INSERT INTO jc_contact(email, comment) VALUES ('" + row.getCell(ExcelParser.emailColumnIndex) + "', '" + comment + "') ON CONFLICT (email) DO UPDATE SET comment = '" + comment + "'");
                            }
                        }

                    }
                }
            }
            statement.close();
            domianFunc();
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    public static void insert(XSSFSheet sheet, FormulaEvaluator formulaEvaluator) {
        if (!ExcelParser.dbtableheaders.containsAll(ExcelParser.excelheaders)) {
            try {
                Statement statement = DBConnect.connection.createStatement();
                statement.executeUpdate("DROP TABLE " + "jc_contact" + "");  // Drop old table from database.
                statement.close();
            } catch (SQLException ignored) {
            }
            TableCreator.jcContactTable(); //Create table and columns.
        }
        inserter(sheet);
    }

//    public static HashMap<String, Integer> getStatistics() {
//        return domain_counter;
//    }
}
