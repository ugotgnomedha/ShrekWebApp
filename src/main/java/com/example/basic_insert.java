package com.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.sql.SQLException;

public class basic_insert extends excel_parser {
    public static void inserter(XSSFSheet sheet) throws SQLException {
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) { // Row switching
            Row row = sheet.getRow(i);
            int j = 0;
            for (Cell cell : row) {
                statement.executeUpdate("INSERT INTO jc_contact(id, " + headers.get(j) + ") VALUES(" + i + ", '" + cell + "') ON CONFLICT (id) DO UPDATE SET " + headers.get(j) + " = '" + cell + "'");
                j++;
            }
        }
    }
}
