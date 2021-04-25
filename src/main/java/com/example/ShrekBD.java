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

//    public static void main(String[] args) throws SQLException {
//
//
//        try {
//
//            if (connection != null) {
//                System.out.println("Connected to PostgreSQL server successfully!");
//            } else {
//                System.out.println("Failed to connect PostgresSQL server");
//            }
//            Statement stmt = connection.createStatement();
//
//
////            ResultSet rs = stmt.executeQuery("SELECT * FROM JC_CONTACT");
////
////            while (rs.next()) {
////                String str = rs.getString("contact_id") + ":" + rs.getString(2);
////                System.out.println("Contact:" + str);
////            }
//
//            addData(connection);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//    }

    public void addData(Connection con) throws SQLException {
        try (PreparedStatement Illstmt = con.prepareStatement(
                "INSERT INTO jc_contact (name, sex, age, phone, email) "
                        + "VALUES (?, ?, ?, ?, ?)")) {
            Statement stmt = con.createStatement();

//            for (int i = 0; i < 10; i++) {
//                stmt.setString(1, "FirstName_" + i);
//                stmt.setString(2, "LastNAme_" + i);
//                stmt.setString(3, "phone_" + i);
//                stmt.setString(4, "email_" + i);
//                stmt.addBatch();
//            }

//            System.out.println("Пропишите путь");
//            Scanner file_scan = new Scanner(System.in);
//            String file_path1 = file_scan.nextLine();
            File exel_file_first = new File("D:\\Files\\Programming\\projects\\JavaProgramming\\ShrekSystem\\data.xlsx");
            FileInputStream fis = new FileInputStream(exel_file_first);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet file_sheet = wb.getSheetAt(0);

            //int rowInt = file_sheet.getPhysicalNumberOfRows();
            //int columnsInt = file_sheet.getRow(0).getPhysicalNumberOfCells();


            int i = 0;
            final String spec_sym = "$aesc6$";

            Value nv = new Value();

            Iterator<Row> rownum = file_sheet.iterator();

            ArrayList<String> data = new ArrayList<>();


            while (rownum.hasNext()) {
                //Map<String, String> values_storage = new HashMap<String, String>();
                Row rows = rownum.next();
                Iterator<Cell> cellIterator = rows.cellIterator();
                int number = 0;
                while (cellIterator.hasNext()) {
                    if (i == 5) {
//                        stmt.execute("INSERT INTO JC_CONTACT (NAME_ , SEX, AGE, PHONE, EMAIL) VALUES (" + data.get(0) + "," + data.get(1) + "," + data.get(2) + "," + data.get(3) + "," + data.get(4) + ")");
                        stmt.execute("INSERT INTO JC_CONTACT (NAME_ , SEX, AGE, PHONE, EMAIL) VALUES ('Helga','Male', '33', '+79118765432','helga@pisem.net')");
//                        System.out.println(data.get(i - 1));
                        stmt.execute("INSERT INTO JC_CONTACT (NAME_ , SEX, AGE, PHONE, EMAIL) VALUES (" + spec_sym + data.get(0) + spec_sym + "," + spec_sym + data.get(1) + spec_sym + "," + spec_sym + data.get(2) + spec_sym + "," + spec_sym + data.get(3) + spec_sym + "," + spec_sym + data.get(4) + spec_sym + ")");

//                        System.out.println("INSERT INTO JC_CONTACT (NAME_ , SEX, AGE, PHONE, EMAIL) VALUES (" + spec_sym + data.get(0)+ spec_sym + ","+ spec_sym + data.get(1)+ spec_sym + ","+ spec_sym + data.get(2)+ spec_sym + ","+ spec_sym + data.get(3)+ spec_sym + ","+ spec_sym + data.get(4)+ spec_sym + ")");
                        i = 0;
                        data.clear();
                    }

                    //Check if table exists in database.
                    DatabaseMetaData dbm = connection.getMetaData();
                    ResultSet tables = dbm.getTables(null, null, "jc_contact", null);
                    Cell cell = cellIterator.next();
                    String excel_values = cell.toString();
                    data.add(excel_values);
//                    if (tables.next()) {
////                        System.out.println("We entered if");
//                    String sql_update = "DELETE FROM jc_contact WHERE email = " + spec_sym + "" + excel_values + "" + spec_sym + "";
//                    System.out.println(sql_update);
                    String sql_update = "DELETE FROM jc_contact WHERE email = " + spec_sym + "helga@pisem.net" + spec_sym + "";
                    stmt.execute(sql_update);
//                    }
//                     else {
//                        System.out.println("new table!");
//                        String CreateSql = "Create Table " + "jc_contact" + "(name varchar, sex varchar, age varchar, phone varchar, email varchar)";
//                       stmt.execute(CreateSql);
//                    }
                    System.out.print(excel_values);
                    System.out.println();
                    //nv.header = excel_values;
                    //values_storage.put(nv.header, nv.content);
                    //System.out.println(values_storage);
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
