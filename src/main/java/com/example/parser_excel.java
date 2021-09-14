package com.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class parser_excel extends StartConnection {
    public static int maxcell;
    public static ArrayList<String> headers;

    public static void column_create() {
        try {
            for (int i = 0; i < headers.size(); i++) {
                stat.executeUpdate("ALTER TABLE jc_contact ADD COLUMN " + headers.get(i) + " varchar");
            }
        } catch (SQLException table) {
            table.printStackTrace();
        }
    }

    public static ArrayList<String> getHeaders() {
        return headers;
    }

    public static void parser(File file_get) throws IOException {
        File file = file_get;

        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet file_sheet = wb.getSheetAt(0);
        maxcell = file_sheet.getLastRowNum();
        Iterator<Row> rownum = file_sheet.iterator();
        Row rows = rownum.next();
        Iterator<Cell> cellIterator = rows.cellIterator();
        headers = new ArrayList<>();

        //Get headers.
        file_sheet.getRow(0);
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String excel_values = cell.toString();
            if (excel_values == "") {
            } else {
                headers.add(excel_values);
            }
        }

        try {
            // Check if table exists.
            DatabaseMetaData dbm = connect.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "jc_contact", null);

            if (tables.next()) {
                stat.executeUpdate("DROP TABLE jc_contact");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            stat.executeUpdate("CREATE TABLE jc_contact(id integer PRIMARY KEY, comment varchar)");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        column_create();

        Row row;
        for (int i = 0; i < headers.size(); i++) {
            for (int j = 1; j < maxcell + 1; j++) {
                row = file_sheet.getRow(j);
                Cell values_cell = row.getCell(i);
                String values = (values_cell == null) ? "-" : values_cell.toString();
                if (values == "") {
                    continue;
                }
                String comment_insert = "";
                if (values.contains("—")) {
                    comment_insert = values.substring(values.indexOf("—") + 1);
                    values = values.substring(0, values.indexOf("—"));
                    values = values.replaceAll("[a-zA-Zа-яА-Я]", "");
                } else if (values.contains("•")) {
                    comment_insert = values.substring(values.indexOf("•") + 1);
                    values = values.substring(0, values.indexOf("•"));
                    values = values.replaceAll("[a-zA-Zа-яА-Я]", "");
                }

                if (comment_insert != "") {
                    try {
                        stat.executeUpdate("INSERT INTO jc_contact(id, " + headers.get(i) + ", comment) VALUES('" + j + "' ,'" + values + "', '" + comment_insert + "') ON CONFLICT (id) DO UPDATE SET comment = '" + comment_insert + "'");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }

                try {
                    String insert = "INSERT INTO jc_contact(id, " + headers.get(i) + ") VALUES('" + j + "' ,'" + values + "') ON CONFLICT (id) DO UPDATE SET " + headers.get(i) + " = '" + values + "'";
                    stat.executeUpdate(insert);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
