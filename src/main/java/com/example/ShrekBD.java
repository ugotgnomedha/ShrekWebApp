package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.Constants.*;

public class ShrekBD {

    public static Statement stmt;
    public static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void deleteDuples(String email, List<String> data) throws SQLException {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + mainDataBaseName + " WHERE email LIKE '" + email + "' ");
        List<Map> items = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        String phoneLine = "";
        int i = 1;
        while (rs.next()) {
            Map<String, String> item = new HashMap<>();
            item.put("index", String.valueOf(i));
            item.put("name", rs.getString("name_"));
            item.put("sex", rs.getString("sex"));
            item.put("age", rs.getString("age"));
            item.put("phone", rs.getString("phone"));
            item.put("email", rs.getString("email"));
            items.add(item);
            i++;
        }
        for (int t = 0; t < items.size(); t++) {
            String testPhone = String.valueOf(items.get(t).get("phone"));
            boolean flag = false;
            if (!phoneLine.equals("")) {
                flag = true;
            }
            if (!phoneLine.contains(testPhone)) {
                if (flag) {
                    phoneLine = phoneLine + " • " + testPhone;
                } else {
                    phoneLine = phoneLine + testPhone;
                }

            }

        }
        String sql_update = "DELETE FROM " + mainDataBaseName + " WHERE email = " + spec_sym + email + spec_sym + "";
        stmt.execute(sql_update);
        stmt.execute("INSERT INTO " + mainDataBaseName + " (NAME_ , SEX, AGE, PHONE, EMAIL) VALUES (" + spec_sym + data.get(0) + spec_sym + "," + spec_sym + data.get(1) + spec_sym + "," + spec_sym + data.get(2) + spec_sym + "," + spec_sym + phoneLine + spec_sym + "," + spec_sym + data.get(4) + spec_sym + ")");
    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public List<HashMap<String, String>> getListOfData() throws SQLException {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + mainDataBaseName + " ORDER BY Домены ASC, Фио ASC;");
        List<HashMap<String, String>> items = new ArrayList<>();
        int i = 1;
        while (rs.next()) {
            HashMap<String, String> item = new HashMap<>();
            item.put("index", String.valueOf(i));
            item.put("comment", rs.getString(1));
            item.put("name", rs.getString(3));
            item.put("sex", rs.getString(4));
            item.put("age", rs.getString(5));
            item.put("phone", rs.getString(6));
            item.put("email", rs.getString(7));
            items.add(item);
            i++;
        }
        return items;
    }

    public void export(String path) throws SQLException {
        stmt = connection.createStatement();
        stmt.execute("COPY " + mainDataBaseName + " TO " + "'" + path + "\\\\" + mainDataBaseName + ".csv" + "'" + " DELIMITER " + " ','" + " CSV HEADER;");
    }

    public void exportPreSet(String path, List<String> listOfEmails) throws SQLException {
        stmt = connection.createStatement();
        stmt.execute("DROP TABLE " + temporaryDataBaseName + "");
        stmt.execute("CREATE TABLE " + temporaryDataBaseName + " AS TABLE " + mainDataBaseName + " WITH NO DATA;");
        for (String email : listOfEmails) {
            stmt.execute(" INSERT INTO " + temporaryDataBaseName + " SELECT * FROM " + mainDataBaseName + " WHERE e_test LIKE '" + "%" + email + "%" + "' ");
        }
        stmt.execute("COPY " + temporaryDataBaseName + " TO " + "'" + path + "\\\\PreSetedData.csv" + "'" + " DELIMITER " + " ','" + " CSV HEADER;");

    }

    public void drop() throws SQLException {
        stmt = connection.createStatement();
        stmt.execute("delete from " + mainDataBaseName + ";");
    }

    public static String quote(String s) {
        return new StringBuilder()
                .append('\'')
                .append(s)
                .append('\'')
                .toString();
    }

    public List<HashMap<String, String>> getPreSets() throws FileNotFoundException {
        final String dir = System.getProperty("user.dir");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Reader reader = new FileReader(dir + "\\preSets\\staff.json")) {

            // Convert JSON to JsonElement, and later to String
            JsonElement json = gson.fromJson(reader, JsonElement.class);
            String jsonInString = gson.toJson(json);
            User[] userArray = gson.fromJson(jsonInString, User[].class);
            List<HashMap<String, String>> items = new ArrayList<>();
            for (User user : userArray) {
                HashMap<String, String> item = new HashMap<>();
                item.put("name", user.getName());
                item.put("sets", user.getSets());
                items.add(item);
            }
            return items;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void applyLiveEdit(String key, String name, String sex, String age, String phone, String email, String comment) throws SQLException {
        ArrayList<String> column_names = ExelParser.getColumnNames();
        stmt = connection.createStatement();
        System.out.println(column_names.get(6));
        stmt.executeUpdate("UPDATE " + mainDataBaseName + " SET " + column_names.get(0) + " = " + quote(comment) + ", " + column_names.get(2) + " = " + quote(name) + ", " + column_names.get(3) + " = " + quote(sex) + ", " + column_names.get(4) + " = " + quote(age) + ", " + column_names.get(5) + " = " + quote(phone) + ", " + column_names.get(6) + " = " + quote(email) + " WHERE " + column_names.get(6) + " = " + quote(key));
        System.out.println("UPDATE " + mainDataBaseName + " SET " + column_names.get(0) + " = " + quote(comment) + ", " + column_names.get(2) + " = " + quote(name) + ", " + column_names.get(3) + " = " + quote(sex) + ", " + column_names.get(4) + " = " + quote(age) + ", " + column_names.get(5) + " = " + quote(phone) + ", " + column_names.get(6) + " = " + quote(email) + " WHERE " + column_names.get(6) + " = " + quote(key));

    }

    public static List<HashMap<String, String>> getSortedListOfData() throws SQLException {
        stmt = connection.createStatement();
        stmt.execute("INSERT INTO sorted SELECT * FROM jc_contact ORDER BY Домены asc;");
        ResultSet rs = stmt.executeQuery("select * from " + "sorted" + ";");
        List<HashMap<String, String>> items = new ArrayList<>();
        int i = 1;
        while (rs.next()) {
            HashMap<String, String> item = new HashMap<>();
            item.put("index", String.valueOf(i));
            item.put("comment", rs.getString(1));
            item.put("name", rs.getString(3));
            item.put("sex", rs.getString(4));
            item.put("age", rs.getString(5));
            item.put("phone", rs.getString(6));
            item.put("email", rs.getString(7));
            items.add(item);
            i++;
        }
        return items;

    }

}
