package com.example;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ExcelParser {

    private static final Logger logger = LogManager.getLogger(ExcelParser.class);
    public static Integer emailColumnIndex = 0;
    public static ArrayList<String> excelheaders = new ArrayList<>();
    public static ArrayList<String> dbtableheaders = new ArrayList<>();
    public static int column_count = 0;
    public static String emailNameFromExcel = "";

    public static boolean excelInitializer() {
        boolean newTable = false;
        try {
            emailColumnIndex = 0;
            column_count = 0;
            excelheaders.clear();
            dbtableheaders.clear();
            ShrekBD shrek = new ShrekBD();
            final String dir = System.getProperty("user.dir");
            FileInputStream fis = new FileInputStream(dir + "/upload-dir/" + shrek.listFilesUsingDirectoryStream(dir + "/upload-dir").get(0));
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            headerExcelGetter(sheet); //Get excel table headers.
            headerDBtableGetter(); //Get database table headers.
            newTable = ExcelDataInserter.columnCheck(sheet); //Insert excel data into a database table.

            excelheaders.add(0, "comment");   // Dumbest thing that is needed because something in the
            // front of the program utilized this variable wrongly and now this needs additional comment value...
        } catch (FileNotFoundException exception) {
            logger.error("Error occurred while accessing excel file. Possibly the file path is incorrect.");
            exception.printStackTrace();
        } catch (IOException ignored) {
        }
        return newTable;
    }

    public static void headerDBtableGetter() {
        try {
            Statement statement = DBConnect.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT column_name FROM information_schema.columns WHERE table_name = '" + "jc_contact" + "';");
            while (rs.next()) {
                dbtableheaders.add(rs.getString(1));
            }
            rs.close();
            statement.close();
        } catch (Exception exception) {
            logger.error("Error occurred while getting headers from database table.");
            exception.printStackTrace();
        }
    }

    public static void headerExcelGetter(XSSFSheet sheet) {
        try {
            Row row = sheet.getRow(0);
            for (Cell cell : row) {
                if (cell.toString().contains("email") || cell.toString().contains("Email")
                        || cell.toString().contains("почта") || cell.toString().contains("Почта") || cell.toString().contains("e-mail")) {
                    emailColumnIndex = cell.getColumnIndex();
                    excelheaders.add(headersTransform.TranslateNameToDB(cell.toString().toLowerCase()));
                    emailNameFromExcel = headersTransform.TranslateNameToDB(cell.toString().toLowerCase());
                    column_count++;
                } else if (!cell.toString().equals("")) {
                    excelheaders.add(headersTransform.TranslateNameToDB(cell.toString().toLowerCase()));
                    column_count++;
                }
            }
        } catch (Exception exception) {
            logger.error("Error occurred while getting headers from excel sheet.");
            exception.printStackTrace();
        }
    }

}
