package com.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.Constants.*;

public class ExelParser extends StartConnection {
    final static String dir = System.getProperty("user.dir");
    final static File folder = new File(dir + "\\upload-dir");

    public static String comment_insert;
    public static String domain_insert;
    public static ArrayList<String> column_names = new ArrayList<>();
    public static String header_name;

    public static ArrayList<String> getColumnNames() throws SQLException {
        ArrayList<String> columnNames = new ArrayList<>();
        ResultSet rs = stat.executeQuery("SELECT * FROM " + mainDataBaseName);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        // The column count starts from 1
        for (int num = 1; num <= columnCount; num++) {
            String name = rsmd.getColumnName(num);
            // Do stuff with name
            columnNames.add(name);
        }
        return columnNames;
    }


    public static void TranslateNameToDB(String header) {
        header = header.replace(" ", "_");
        header = header.replace("(", "");
        header = header.replace(")", "");
        header = header.replace(".", "");
        header = header.replace("/", "_");
        header = header.replace("-", "_");
        header = header.replace("&", "_and_");
        header = header.replace("%", "percent");
        header = header.replace(",", "");
        header_name = header;
        column_names.add(header);
    }

    public static void createColumns(XSSFSheet fileSheet) {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Statement st_ = conn.createStatement();
            stat = st_;
            stat.executeUpdate("ALTER TABLE " + mainDataBaseName + " ADD Комментарии varchar");
            stat.executeUpdate("ALTER TABLE " + mainDataBaseName + " ADD Домены varchar");
        } catch (SQLException ex) {
        }

        Iterator<Row> rownum = fileSheet.iterator();
        Row rows = rownum.next();
        Iterator<Cell> cellIterator = rows.cellIterator();
        int number = 0;
        ArrayList<String> data_headers = new ArrayList<>();
        while (cellIterator.hasNext()) {
            //Adding cell value to a string.
            Cell cell = cellIterator.next();
            String cell_value = cell.toString();
            data_headers.add(cell_value);
            TranslateNameToDB(data_headers.get(number));
            if (header_name.contains("e_test") || header_name.contains("email")) {
                //stat.executeQuery("ALTER TABLE " + mainDataBaseName + " ADD  " + header_name + " varchar PRIMARY KEY ");
                try {
                    ResultSet result = stat.executeQuery("ALTER TABLE " + mainDataBaseName + " ADD  " + header_name + " varchar PRIMARY KEY");
                    while (result.next()) {
                    }
                    result.close();
                } catch (SQLException exception) {
                }
            } else {
                try {
                    ResultSet result = stat.executeQuery("ALTER TABLE " + mainDataBaseName + " ADD  " + header_name + " varchar ");
                    while (result.next()) {
                    }
                    result.close();
                } catch (SQLException ex) {
                }
            }
            number++;
        }
    }

    public static void parseExel() {

        File exel_file_first = new File(getDataFilePath());

        try {
            FileInputStream fis = new FileInputStream(exel_file_first);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet file_sheet = wb.getSheetAt(0);

            //Creating iterators for rows and cells.
            Iterator<Row> rownum = file_sheet.iterator();

            //get number of columns.
            //int column_num = file_sheet.getRow(0).getPhysicalNumberOfCells();

            //Check if table exists in database.
            DatabaseMetaData dbm = connect.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "" + mainDataBaseName + "", null);

            if (tables.next()) {
                int i = 0;
                Row rows = rownum.next();
                Iterator<Cell> cellIterator = rows.cellIterator();

                ArrayList<String> data_headers = new ArrayList<>();
                while (cellIterator.hasNext()) {
                    //Adding cell value to a string.
                    Cell cell = cellIterator.next();
                    String cell_value = cell.toString();
                    data_headers.add(cell_value);
                    TranslateNameToDB(data_headers.get(i));
                    i++;
                }
                i = 0;
                createColumns(file_sheet);
                int columns_number = file_sheet.getRow(0).getLastCellNum();
                ArrayList<String> data_cells = new ArrayList<>();
                while (rownum.hasNext()) {
                    Row rows_ = rownum.next();
                    Iterator<Cell> cellIterator_ = rows_.cellIterator();
                    while (cellIterator_.hasNext()) {
                        Cell cell = cellIterator_.next();
                        String cell_value = cell.toString();
                        if (i == 3) {
                            cell_value = cell_value.replaceAll("-", "");
                            cell_value = cell_value.replace("+", "");
                            if (cell_value.contains("—")) {
                                String comment = cell_value.substring(cell_value.indexOf("—"));
                                comment_insert = comment;
                                cell_value = cell_value.substring(0, cell_value.indexOf("—"));
                            } else if (cell_value.contains("•")) {
                                String comment_ = cell_value.substring(cell_value.indexOf("•"));
                                comment_insert = comment_;
                                cell_value = cell_value.substring(0, cell_value.indexOf("•"));
                            }
                            cell_value = cell_value.replaceAll("[a-zA-Zа-яА-Я]", "");
                        }
                        if (i == 4 && cell_value.contains("@")) {
                            String domain = cell_value.substring(cell_value.indexOf("@") + 1);
                            domain_insert = domain;
                        }
                        data_cells.add(cell_value);
                        i++;
                    }
                    if (i == columns_number) {
                        stat.executeUpdate("INSERT INTO " + mainDataBaseName + " (Домены, Комментарии, " + column_names.get(0) + " , " + column_names.get(1) + ", " + column_names.get(2) + ", " + column_names.get(3) + ", " + column_names.get(4) + ") VALUES ('" + domain_insert + "','" + comment_insert + "', '" + data_cells.get(0) + "','" + data_cells.get(1) + "','" + data_cells.get(2) + "','" + data_cells.get(3) + "','" + data_cells.get(4) + "') ON CONFLICT (" + column_names.get(4) + ") DO UPDATE SET " + column_names.get(4) + " = '" + data_cells.get(4) + "', " + column_names.get(3) + " = '•" + data_cells.get(3) + "'");
                        comment_insert = "";
                        i = 0;
                        data_cells.clear();
                    }
                }


            } else { //get the first line and make headers out of it.
                System.out.println("creating table!");
                stat.execute("CREATE TABLE " + mainDataBaseName + "()");
                createColumns(file_sheet);
                int columns_number = file_sheet.getRow(0).getLastCellNum();
                int number = 0;
                ArrayList<String> data_cells = new ArrayList<>();
                while (rownum.hasNext()) {
                    Row rows_ = rownum.next();
                    Iterator<Cell> cellIterator_ = rows_.cellIterator();
                    while (cellIterator_.hasNext()) {
                        Cell cell = cellIterator_.next();
                        String cell_value = cell.toString();
                        if (number == 3) {
                            cell_value = cell_value.replaceAll("-", "");
                            cell_value = cell_value.replace("+", "");
                            if (cell_value.contains("—")) {
                                String comment = cell_value.substring(cell_value.indexOf("—"));
                                comment_insert = comment;
                                cell_value = cell_value.substring(0, cell_value.indexOf("—"));
                            } else if (cell_value.contains("•")) {
                                String comment_ = cell_value.substring(cell_value.indexOf("•"));
                                comment_insert = comment_;
                                cell_value = cell_value.substring(0, cell_value.indexOf("•"));
                            }
                            cell_value = cell_value.replaceAll("[a-zA-Zа-яА-Я]", "");
                        }
                        data_cells.add(cell_value);
                        number++;
                    }
                    if (number == columns_number) {
                        stat.executeUpdate("INSERT INTO " + mainDataBaseName + " (Домены, Комментарии, " + column_names.get(0) + " , " + column_names.get(1) + ", " + column_names.get(2) + ", " + column_names.get(3) + ", " + column_names.get(4) + ") VALUES ('" + domain_insert + "','" + comment_insert + "', '" + data_cells.get(0) + "','" + data_cells.get(1) + "','" + data_cells.get(2) + "','" + data_cells.get(3) + "','" + data_cells.get(4) + "') ON CONFLICT (" + column_names.get(4) + ") DO UPDATE SET " + column_names.get(4) + " = '" + data_cells.get(4) + "', " + column_names.get(3) + " = '•" + data_cells.get(3) + "'");
                        comment_insert = "";
                        number = 0;
                        data_cells.clear();
                    }
                }
            }

        } catch (IOException ioex) {
            ioex.printStackTrace();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }

    public static void search(final String pattern, final File folder, List<String> result) {
        for (final File f : folder.listFiles()) {

            if (f.isDirectory()) {
                search(pattern, f, result);
            }

            if (f.isFile()) {
                if (f.getName().matches(pattern)) {
                    result.add(f.getAbsolutePath());
                }
            }

        }
    }

    public static String getDataFilePath() {
        List<String> result = new ArrayList<>();

        search(".*\\.xlsx", folder, result);

        return result.get(0);
    }
}