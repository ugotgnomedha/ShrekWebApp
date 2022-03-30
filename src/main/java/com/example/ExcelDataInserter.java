package com.example;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.Constants.*;

public class ExcelDataInserter {
    private static final Logger logger = LogManager.getLogger(ExcelDataInserter.class);

    public static HashMap<String, String> phoneRegionFunc(String columnName) {
        HashMap<String, String> phone_region = new HashMap<>();
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT " + columnName + " FROM jc_contact");
            while (rs.next()) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                try {
                    Phonenumber.PhoneNumber numberProto = phoneUtil.parse(rs.getString(1), "RU");
                    phone_region.put(rs.getString(1), numberProto.toString());
                } catch (NumberParseException e) {
                    logger.error("Error parsing phone number.");
                }
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phone_region;
    }

    public static HashMap<String, Integer> domianFunc() {
        HashMap<String, Integer> domain_counter = new HashMap<>();
        List<String> emailSynonyms = new ArrayList<>();
        emailSynonyms.add("email");
        emailSynonyms.add("почта");
        emailSynonyms.add("почтовый ящик");
        emailSynonyms.add("e-mail");
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            String emailName = ExcelParser.emailNameFromExcel;
            if(emailName.equals("")){
                ArrayList<String> headers = null;
                try {
                    Statement stat = connection.createStatement();
                    headers = new ArrayList<>();
                    headers = new ArrayList<String>();
                    ResultSet rsH = stat.executeQuery("SELECT *\n" +
                            "  FROM information_schema.columns\n" +
                            " WHERE table_schema = 'public'\n" +
                            "   AND table_name   = 'jc_contact'\n" +
                            "     ;");
                    while (rsH.next()) {
                        headers.add(rsH.getString("column_name"));
                    }
                    rsH.close();
                    stat.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for(String header: headers){
                    if(emailSynonyms.contains(header)){
                        emailName = header;
                        break;
                    }
                }
            }

            ResultSet rs = statement.executeQuery("SELECT " + emailName + " FROM jc_contact");
            while (rs.next()) {
                try {
                    if (rs.getString(1).contains("@")) {
                        String domain_full = rs.getString(1).substring(rs.getString(1).indexOf("@"));
                        domain_full = domain_full.replaceAll(" ", "");
                        String domain = domain_full;
//                        if (domain_full.contains(".")) {
//                            domain = domain_full.substring(domain_full.indexOf("."));
//                        } else {
//                            domain = domain_full;
//                        }
                        // get the value of the specified domain.
                        Integer count = domain_counter.get(domain);
                        if (domain_counter.containsKey(domain)) {
                            domain_counter.put(domain, count + 1);
                        } else {
                            domain_counter.put(domain, 1);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            logger.error("Invalid email format");
            e.printStackTrace();
        }
        return domain_counter;
    }

    public static void columnCheck(XSSFSheet sheet) {
        try {
            if (!ExcelParser.dbtableheaders.containsAll(ExcelParser.excelheaders)) {
                try {
                    Statement statement = DBConnect.connection.createStatement();
                    statement.executeUpdate("DROP TABLE " + "jc_contact" + "");  // Drop old table from database.
                    statement.close();
                } catch (SQLException ignored) {
                }
                TableCreator.jcContactTable(); //Create table and columns.
            }
            bulkInsertAssembler(sheet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void bulkInsertAssembler(XSSFSheet sheet) {
        try {
            String insertSql = "INSERT INTO jc_contact" + "  (" + String.join(", ", ExcelParser.excelheaders) + ", comment) VALUES (-) ON CONFLICT (" + ExcelParser.emailNameFromExcel + ") DO NOTHING";
            for (int i = 0; i < ExcelParser.excelheaders.size(); i++) {
                insertSql = insertSql.replaceAll("-", "?, -");
            }
            insertSql = insertSql.replace(", -", ", ?"); // Last one for comment.
            //System.out.println(insertSql);

            int count = 1;

            PreparedStatement preparedStatement = DBConnect.connection.prepareStatement(insertSql);
            DBConnect.connection.setAutoCommit(false);

            int rowCount = sheet.getLastRowNum();
            for (int j = 1; j < rowCount; j++) {  // Switch rows.
                String comment = "-";
                for (int i = 0; i < ExcelParser.column_count; i++) {  // Switch columns.
                    Cell cell = sheet.getRow(j).getCell(i);
                    String value = (cell == null) ? "-" : cell.toString();
                    if (value.contains("—")) {
                        comment = value.substring(value.indexOf("—") + 1);
                        value = value.substring(0, value.indexOf("—"));
                    } else if (value.contains("•")) {
                        comment = value.substring(value.indexOf("•") + 1);
                        value = value.substring(0, value.indexOf("•"));
                    }
                    preparedStatement.setString(count, value);
                    if (count == ExcelParser.column_count) {
                        count = 0;
                    }
                    count++;
                }
                preparedStatement.setString(ExcelParser.column_count + 1, comment);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();
            DBConnect.connection.commit();
            DBConnect.connection.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
