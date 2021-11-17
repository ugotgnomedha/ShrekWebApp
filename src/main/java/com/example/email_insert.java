package com.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.sql.SQLException;

public class email_insert extends excel_parser {

    public static void inserter(int column_count, XSSFSheet sheet, FormulaEvaluator formulaEvaluator) throws SQLException {
        //email_insert(sheet);
        for (int i = 0; i < column_count; i++) {
            for (Row row : sheet) {
                Cell cell_email = row.getCell(emailColumnIndex);
                statement.executeUpdate("INSERT INTO jc_contact(email) VALUES('" + cell_email + "') ON CONFLICT (email) DO NOTHING");
                if (headers.get(i).equals("Email") || headers.get(i).equals("email")) {
                    continue;
                }
                Cell cell = row.getCell(i);
                System.out.println(cell.toString());
                if (formulaEvaluator.evaluateInCell(cell).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    statement.executeUpdate("INSERT INTO jc_contact(email, " + headers.get(i) + ") VALUES ('" + cell_email + "', '" + cell.getNumericCellValue() + "') ON CONFLICT (email) DO UPDATE SET " + headers.get(i) + " = '" + cell.getNumericCellValue() + "'");
                } else {
                    statement.executeUpdate("INSERT INTO jc_contact(email, " + headers.get(i) + ") VALUES ('" + cell_email + "', '" + cell.getStringCellValue() + "') ON CONFLICT (email) DO UPDATE SET " + headers.get(i) + " = '" + cell.getStringCellValue() + "'");

                }
            }
        }
    }
}
