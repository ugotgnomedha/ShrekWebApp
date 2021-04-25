package com.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ShrekBD {

    public ShrekBD() {
    }

    static class Value {
        public String header;
        public String content;
    }

    private static final String url = "jdbc:postgresql://localhost/postgres";
    private static final String user = "postgres";
    private static final String password = "root";

    public static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    ;


    public void addData(Connection con) throws SQLException {
        try (PreparedStatement Illstmt = con.prepareStatement(
                "INSERT INTO jc_contact (name, sex, age, phone, email) "
                        + "VALUES (?, ?, ?, ?, ?)")) {
            Statement stmt = con.createStatement();
            File exel_file_first = new File("D:\\Files\\Programming\\projects\\JavaProgramming\\ShrekWebApp\\dataShort.xlsx");
            FileInputStream fis = new FileInputStream(exel_file_first);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet file_sheet = wb.getSheetAt(0);


            int i = 0;
            final String spec_sym = "$aesc6$";

            Value nv = new Value();

            Iterator<Row> rownum = file_sheet.iterator();

            ArrayList<String> data = new ArrayList<>();


            while (rownum.hasNext()) {
                Row rows = rownum.next();
                Iterator<Cell> cellIterator = rows.cellIterator();
                int number = 0;
                while (cellIterator.hasNext()) {
                    if (i == 5) {
                        stmt.execute("INSERT INTO JC_CONTACT (NAME_ , SEX, AGE, PHONE, EMAIL) VALUES (" + spec_sym + data.get(0) + spec_sym + "," + spec_sym + data.get(1) + spec_sym + "," + spec_sym + data.get(2) + spec_sym + "," + spec_sym + data.get(3) + spec_sym + "," + spec_sym + data.get(4) + spec_sym + ")");
                        i = 0;
                        data.clear();
                    }

                    DatabaseMetaData dbm = connection.getMetaData();
                    ResultSet tables = dbm.getTables(null, null, "jc_contact", null);
                    Cell cell = cellIterator.next();
                    String excel_values = cell.toString();
                    data.add(excel_values);
                    String sql_update = "DELETE FROM jc_contact WHERE email = " + spec_sym + "helga@pisem.net" + spec_sym + "";
                    stmt.execute(sql_update);
                    System.out.print(excel_values);
                    System.out.println();
                    i++;
                }
            }
            ResultSet gk = stmt.getGeneratedKeys();
            while (gk.next()) {
                System.out.println("Inserted:" + gk.getString(1));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
