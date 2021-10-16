package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

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
            item.put("Фио", rs.getString("name_"));
            item.put("Пол", rs.getString("sex"));
            item.put("Возраст", rs.getString("age"));
            item.put("Телефон", rs.getString("phone"));
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

    public List<HashMap<String, String>> getSortedListOfData() throws SQLException {
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + mainDataBaseName + " ORDER BY email ASC, Фио ASC;");
        List<HashMap<String, String>> items = new ArrayList<>();
        int i = 1;
        if (parser_excel.getHeaders() != null) {
            while (rs.next()) {
                HashMap<String, String> item = new HashMap<>();
                item.put("index", String.valueOf(i));
                for (String header : parser_excel.getHeaders()) {
                    item.put(header, rs.getString(header));
                }


//            item.put("comment", rs.getString(1));
//            item.put("name", rs.getString(3));
//            item.put("sex", rs.getString(4));
//            item.put("age", rs.getString(5));
//            item.put("phone", rs.getString(6));
//            item.put("email", rs.getString(7));
                items.add(item);
                i++;
            }
        }
        return items;
    }

    public List<List<HashMap<String, String>>> getSortedListOfDataImpact() throws SQLException {
        stmt = connection.createStatement();
        List<List<HashMap<String, String>>> items = new ArrayList<>();
        int i = 1;
        Boolean onlineExists = false;
        List<String> headers = parser_excel.getHeaders();
        if (headers == null) {
            headers = getOnlineTableHeaders();
            onlineExists = true;
        }

        if (headers.size() > 0) {
            ResultSet rs = stmt.executeQuery("select * from " + mainDataBaseName + " ORDER BY email ASC;");
            while (rs.next()) {
                List<HashMap<String, String>> mData = new ArrayList<>();
                HashMap<String, String> indexH = new HashMap<>();
                if (!onlineExists) {
                    indexH.put("Data", String.valueOf(i));
                    mData.add(indexH);
                }


                for (String header : headers) {
                    HashMap<String, String> item = new HashMap<>();
                    if (rs.getString(header) == null) {
                        item.put("Data", "-");
                    } else {
                        item.put("Data", rs.getString(header));
                    }

                    mData.add(item);
                }
                items.add(mData);
                i++;
            }
        } else {

        }
        return items;
    }

    public void export(String path) throws SQLException {
        stmt = connection.createStatement();
        stmt.execute("COPY " + mainDataBaseName + " TO " + "'" + path + "\\\\" + mainDataBaseName + ".csv" + "'" + " DELIMITER " + " ','" + " CSV HEADER;");
    }

    public static ArrayList<String> getOnlineTableHeaders() throws SQLException {
        stmt = connection.createStatement();
        ArrayList<String> headers = new ArrayList<>();
        headers = new ArrayList<String>();
        ResultSet rsH = stmt.executeQuery("SELECT *\n" +
                "  FROM information_schema.columns\n" +
                " WHERE table_schema = 'public'\n" +
                "   AND table_name   = 'jc_contact'\n" +
                "     ;");
        while (rsH.next()) {
            headers.add(rsH.getString("column_name"));
        }
        return headers;
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

    public List<HashMap<String, String>> getDomens() throws FileNotFoundException {
        final String dir = System.getProperty("user.dir");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Reader reader = new FileReader(dir + "\\preSets\\domens.json")) {

            // Convert JSON to JsonElement, and later to String
            JsonElement json = gson.fromJson(reader, JsonElement.class);
            String jsonInString = gson.toJson(json);
            User[] userArray = gson.fromJson(jsonInString, User[].class);
            List<HashMap<String, String>> items = new ArrayList<>();
            for (User user : userArray) {
                HashMap<String, String> item = new HashMap<>();
                item.put("name", user.getName());
                items.add(item);
            }
            return items;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void applyLiveEdit(String stringToEdit) throws SQLException {
        ArrayList<String> data = new ArrayList<>(Arrays.asList(stringToEdit.split("##")));
        System.out.println(data);
        ArrayList<String> column_names = column_names = getOnlineTableHeaders();
        stmt = connection.createStatement();
        int j = 1;
        int i = 0;
        for (String column : column_names) {
            System.out.println("update jc_contact set " + column_names.get(j) + " = " + quote(data.get(j)) + " where id = " + quote(data.get(0)));
            stmt.executeUpdate("update jc_contact set " + column_names.get(j) + " = " + quote(data.get(j)) + " where id = " + quote(data.get(0)));
            if (j == data.size()-1) {
                break;
            }

            j++;
        }

//        stmt.executeUpdate( );
    }

    public ArrayList<String> listFilesUsingDirectoryStream(String dir) throws IOException {
        ArrayList<String> fileList = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileList.add(path.getFileName()
                            .toString());
                }
            }
        }
        return fileList;
    }


}
