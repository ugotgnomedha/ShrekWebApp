package com.example;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ExcelParser {

    private static final Logger logger = LogManager.getLogger(ExcelParser.class);
    public static Integer emailColumnIndex = 0;
    public static ArrayList<String> excelheaders = new ArrayList<>();
    public static ArrayList<String> dbtableheaders = new ArrayList<>();

    public static void excelInitializer() {
        try {
            excelheaders.clear();
            dbtableheaders.clear();
            ShrekBD shrek = new ShrekBD();
            final String dir = System.getProperty("user.dir");
            FileInputStream fis = new FileInputStream(dir + "/upload-dir/" + shrek.listFilesUsingDirectoryStream(dir + "/upload-dir").get(0));
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
            headerExcelGetter(sheet); //Get excel table headers.
            headerDBtableGetter(); //Get database table headers.
            ExcelDataInserter.columnCheck(sheet); //Insert excel data into a database table.
        } catch (FileNotFoundException exception) {
            logger.error("Error occurred while accessing excel file. Possibly the file path is incorrect.");
            exception.printStackTrace();
        } catch (IOException ignored) {
        }
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
                if (cell.getStringCellValue().equals("email") || cell.getStringCellValue().equals("Email")
                        || cell.getStringCellValue().equals("почта") || cell.getStringCellValue().equals("Почта")) {
                    emailColumnIndex = cell.getColumnIndex();
                }
                excelheaders.add(cell.toString().toLowerCase());
            }
        } catch (Exception exception) {
             logger.error("Error occurred while getting headers from excel sheet.");
             exception.printStackTrace();
        }
    }

}
