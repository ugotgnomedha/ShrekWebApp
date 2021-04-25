package com.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

public class ShrekBD {

    final public static String spec_sym = "$aesc6$";
    public static Statement stmt;

    public static String getFileTypeByProbeContentType(File file) {

        System.out.println("Gachi program begins...");

        String fileType = "Undetermined";
        try {
            fileType = Files.probeContentType(file.toPath());
        } catch (IOException ioException) {
            System.out.println("File type not detected for under");
        }


        if (fileType.contains("csv")) {
            System.out.println("CSV!");
            List<List<String>> records = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(", ");
                    records.add(Arrays.asList(values));
                }
                System.out.println(records);
            } catch (Exception excep) {
                excep.printStackTrace();
            }


        } else if (fileType.contains("xlsx") || fileType.contains("xml")) {
            System.out.println("Excel!");
            try {

                FileInputStream fis = new FileInputStream(file);
                XSSFWorkbook wb = new XSSFWorkbook(fis);
                XSSFSheet file_sheet = wb.getSheetAt(0);

                int i = 0;

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
                        String sql_update = "DELETE FROM jc_contact WHERE email = " + spec_sym + excel_values + spec_sym + "";
                        stmt.execute(sql_update);
                        System.out.print(excel_values);
                        System.out.println();
                        i++;


                        final String gmail = "gmail";
                        final String mail = "mail";
                        final String yahoo = "yahoo";
                        final String yandex = "yandex";

                        if (stmt.execute("SELECT email FROM jc_contact WHERE email LIKE ' " + gmail + " ' ")) {
                            DatabaseMetaData dbm_f = connection.getMetaData();
                            ResultSet tables_f = dbm_f.getTables(null, null, "gmail_table", null);
                            if (tables_f.next()) {
                                System.out.println("@gmail table already exists!");
                                String sql_update_f = "INSERT INTO gmail_table (email_gmail) VALUES (" + spec_sym + excel_values + spec_sym + ")";
                                stmt.executeUpdate(sql_update_f);
                            } else {
                                System.out.println("@gmail table doesn't exist!");
                                String CreateSql = "Create Table gmail_table (email_gmail varchar)";
                                stmt.executeUpdate(CreateSql);
                            }


                        } else if (stmt.execute("SELECT email FROM jc_contact WHERE email LIKE ' " + mail + " ' ")) {
                            DatabaseMetaData dbm_s = connection.getMetaData();
                            ResultSet tables_s = dbm_s.getTables(null, null, "mail_table", null);
                            if (tables_s.next()) {
                                System.out.println("@mail table already exists!");
                                String sql_update_s = "INSERT INTO mail_table (email_mail) VALUES (" + spec_sym + excel_values + spec_sym + ")";
                                stmt.execute(sql_update_s);
                            } else {
                                System.out.println("@mail table doesn't exist!");
                                String CreateSql = "Create Table mail_table (email_mail varchar)";
                                stmt.executeUpdate(CreateSql);
                            }

                        } else if (stmt.execute("SELECT email FROM jc_contact WHERE email LIKE ' " + yahoo + " ' ")) {
                            DatabaseMetaData dbm_t = connection.getMetaData();
                            ResultSet tables_t = dbm_t.getTables(null, null, "yahoo_table", null);
                            if (tables_t.next()) {
                                System.out.println("@yahoo table already exists!");
                                String sql_update_t = "INSERT INTO yahoo_table (email_yahoo) VALUES (" + spec_sym + excel_values + spec_sym + ")";
                                stmt.execute(sql_update_t);
                            } else {
                                System.out.println("@mail table doesn't exist!");
                                String CreateSql = "Create Table yahoo_table (email_yahoo varchar)";
                                stmt.executeUpdate(CreateSql);
                            }

                        } else if (stmt.execute("SELECT email FROM jc_contact WHERE email LIKE ' " + yandex + " ' ")) {
                            DatabaseMetaData dbm_fo = connection.getMetaData();
                            ResultSet tables_fo = dbm_fo.getTables(null, null, "mail_table", null);
                            if (tables_fo.next()) {
                                System.out.println("@mail table already exists!");
                                String sql_update_fo = "INSERT INTO mail_table (yandex_mail) VALUES (" + spec_sym + excel_values + spec_sym + ")";
                                stmt.execute(sql_update_fo);
                            } else {
                                System.out.println("@mail table doesn't exist!");
                                String CreateSql = "Create Table yandex_table (yandex_email varchar)";
                                stmt.executeUpdate(CreateSql);
                            }
                        }
                    }
                }


                wb.close();
                fis.close();

            } catch (Exception except) {
                except.printStackTrace();
                System.err.println(except.getClass().getName() + ": " + except.getMessage());
                System.exit(111);
            }


        } else if (fileType.contains("plain")) {
            System.out.println("Text!");
            try {
                Scanner sc = new Scanner(file);

                sc.useDelimiter("\\Z");

                System.out.println(sc.next());

            } catch (FileNotFoundException file_except) {
                file_except.getStackTrace();
            }
        }

        return "Shrek";
    }


    public ShrekBD() throws NoSuchPaddingException, NoSuchAlgorithmException {
    }

    static class Value {
        public String header;
        public String content;
    }

    private static final String url = "jdbc:postgresql://localhost/postgres";
    private static final String user = "postgres";
    private static final String password = "root";
    private static final String key = "Bar12345Bar12345";
    public static Connection connection;
    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
    Cipher cipher = Cipher.getInstance("AES");

    static {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    ;

    public String encrypt(String data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return new String(encrypted);
    }

    public String decrypt(String data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return new String(cipher.doFinal(data.getBytes()));
    }


//    public void addData(Connection con) throws SQLException {
//        try (PreparedStatement Illstmt = con.prepareStatement(
//                "INSERT INTO jc_contact (name, sex, age, phone, email) "
//                        + "VALUES (?, ?, ?, ?, ?)")) {
//            Statement stmt = con.createStatement();
//            File exel_file_first = new File("D:\\Files\\Programming\\projects\\JavaProgramming\\ShrekWebApp\\dataShort.xlsx");
//            FileInputStream fis = new FileInputStream(exel_file_first);
//            XSSFWorkbook wb = new XSSFWorkbook(fis);
//            XSSFSheet file_sheet = wb.getSheetAt(0);
//
//
//            int i = 0;
//            final String spec_sym = "$aesc6$";
//
//            Value nv = new Value();
//
//            Iterator<Row> rownum = file_sheet.iterator();
//
//            ArrayList<String> data = new ArrayList<>();
//
//
//            while (rownum.hasNext()) {
//                Row rows = rownum.next();
//                Iterator<Cell> cellIterator = rows.cellIterator();
//                int number = 0;
//                while (cellIterator.hasNext()) {
//                    if (i == 5) {
//                        stmt.execute("INSERT INTO JC_CONTACT (NAME_ , SEX, AGE, PHONE, EMAIL) VALUES (" + spec_sym + data.get(0) + spec_sym + "," + spec_sym + data.get(1) + spec_sym + "," + spec_sym + data.get(2) + spec_sym + "," + spec_sym + data.get(3) + spec_sym + "," + spec_sym + data.get(4) + spec_sym + ")");
//                        i = 0;
//                        data.clear();
//                    }
//
//                    DatabaseMetaData dbm = connection.getMetaData();
//                    ResultSet tables = dbm.getTables(null, null, "jc_contact", null);
//                    Cell cell = cellIterator.next();
//                    String excel_values = encrypt(cell.toString());
//                    data.add(excel_values);
//                    String sql_update = "DELETE FROM jc_contact WHERE email = " + spec_sym + "helga@pisem.net" + spec_sym + "";
//                    stmt.execute(sql_update);
//                    System.out.print(excel_values);
//                    System.out.println();
//                    i++;
//                }
//            }
//            ResultSet gk = stmt.getGeneratedKeys();
//            while (gk.next()) {
//                System.out.println("Inserted:" + gk.getString(1));
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        }
//    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public void addFile(Connection con, MultipartFile file) throws SQLException, IOException {

        File convertedFile = convertMultiPartToFile(file);

        try (PreparedStatement Illstmt = con.prepareStatement(
                "INSERT INTO jc_contact (name, sex, age, phone, email) "
                        + "VALUES (?, ?, ?, ?, ?)")) {
            Statement stmt_ = con.createStatement();
            stmt = stmt_;

            File exel_file_first = new File("D:\\Files\\Programming\\projects\\JavaProgramming\\ShrekWebApp\\dataShort.xlsx");
            String file_path = "D:\\Files\\Programming\\projects\\JavaProgramming\\ShrekWebApp\\dataShort.xlsx";

            //parsing a file.
            getFileTypeByProbeContentType(convertedFile);


            ResultSet gk = stmt.getGeneratedKeys();
            while (gk.next()) {
                System.out.println("Inserted:" + gk.getString(1));
            }
        }
    }

    public List<Dictionary<String, String>> getListOfData() throws SQLException {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from jc_contact;");
        List<Dictionary<String, String>> items = new ArrayList<>();
        int i = 1;
        while (rs.next()) {
            Dictionary item = new Hashtable<>();
            item.put("index", i);
            item.put("name", rs.getString("name_"));
            item.put("sex", rs.getString("sex"));
            item.put("age", rs.getString("age"));
            item.put("phone", rs.getString("phone"));
            item.put("email", rs.getString("email"));
            items.add(item);
            i++;
        }
        return items;
    }
}
