package com.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static com.example.Constants.*;

// /Users/imac/Desktop/all_coding/ShrekWebApp-master/datafull.xlsx
public class excel_parser {
    public static Connection connection;
    public static Statement statement;
    public static ArrayList<String> headers = new ArrayList<>();
    public static Integer emailColumnIndex;

    public static void connect() {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException sql) {
            sql.printStackTrace();
            // logger.error("Could not connect to database.");
        }
    }

    public static void get_headers(XSSFSheet sheet) {
        try {
            Row row = sheet.getRow(0);
            for (Cell cell : row) {
                if (cell.getStringCellValue().equals("email") || cell.getStringCellValue().equals("Email")) {
                    emailColumnIndex = cell.getColumnIndex();
                    System.out.println(emailColumnIndex);
                }
                headers.add(cell.toString().toLowerCase());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            // logger.error("Error occurred while getting headers from excel sheet.");
        }
    }

    public static void table_create() {
        try {
            statement.executeUpdate("CREATE TABLE jc_contact(id SERIAL UNIQUE)");
            for (String header : headers) {
                if (header.contains("email") || header.contains("Email")) {
                    statement.executeUpdate("ALTER TABLE jc_contact ADD COLUMN email varchar PRIMARY KEY");
                } else if (header.equals("")) {
                    continue;
                } else {
                    statement.executeUpdate("ALTER TABLE jc_contact ADD COLUMN " + header + " varchar");
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            // logger.error("Error occurred while creating new columns.");
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        connect(); // Setting up connection with database.
        ShrekBD shrek = new ShrekBD();
        final String dir = System.getProperty("user.dir");
        if (connection != null) {
            statement = connection.createStatement();
            // Check if table exists.
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "jc_contact", null);
            FileInputStream fis = new FileInputStream(dir + "\\upload-dir\\" + shrek.listFilesUsingDirectoryStream(dir + "\\upload-dir").get(0));
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
            get_headers(sheet); // Getting headers.
            try {
                statement.executeUpdate("DROP TABLE jc_contact");
            } catch (SQLException drop_table) {
                drop_table.printStackTrace();
                //logger.error("Error occurred while dropping a table.")
            }
            if (tables.next()) {
            } else {
                table_create();
                // logger.info("creating new table.");
            }
            int column_count = sheet.getRow(0).getLastCellNum();
            System.out.println(headers);
            if (headers.contains("email") || headers.contains("Email")) {
                email_insert.inserter(column_count, sheet, formulaEvaluator);
            } else {
                basic_insert.inserter(sheet);
            }
        } else {
            System.out.println("No connection to database.");
        }
    }
}
