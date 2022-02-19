package com.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.springframework.core.env.Environment;
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
    public static Integer changes_num = 0;
    public static Integer caller_transaction = 0;

    static {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void save() throws SQLException {
        stmt = connection.createStatement();
        stmt.executeUpdate("COMMIT WORK;");
        caller_transaction = 0;
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
                items.add(item);
                i++;
            }
        }
        return items;
    }

    public List<List<HashMap<String, String>>> getSortedListOfDataImpact() throws SQLException {
        List<List<HashMap<String, String>>> items = null;
        try {
            stmt = connection.createStatement();
            items = new ArrayList<>();
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
                    for (String header : headers) {
                        if (!header.equals("id")) {
                            HashMap<String, String> item = new HashMap<>();
                            if (rs.getString(header) == null) {
                                item.put("Data", "-");
                            } else {
                                item.put("Data", rs.getString(header));
                            }

                            mData.add(item);
                        }
                    }
                    items.add(mData);
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public void export(String path) throws SQLException {
        stmt = connection.createStatement();
        stmt.execute("COPY " + mainDataBaseName + " TO " + "'" + path + mainDataBaseName + ".csv" + "'" + " DELIMITER " + " ','" + " CSV HEADER;");
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
            stmt.execute(" INSERT INTO " + temporaryDataBaseName + " SELECT * FROM " + mainDataBaseName + " WHERE email LIKE '" + "%" + email + "%" + "' ");
        }
        stmt.execute("COPY " + temporaryDataBaseName + " TO " + "'" + path + "\\PreSetedData.csv" + "'" + " DELIMITER " + " ','" + " CSV HEADER;");

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

    public List<HashMap<String, String>> getPreSets() throws IOException {
        final String dir = System.getProperty("user.dir");
        String filePathString = dir + "\\preSets\\staff.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File f = new File(filePathString);
        File folder = new File(dir +
                File.separator + "preSets");
        if (!folder.exists()) {
            folder.mkdir();
        }
        if(!f.exists() || f.isDirectory()) {
            File newDir = new File(dir + "\\preSets");
            File myFile = new File(dir + "\\preSets\\staff.json");
            Writer writer = new FileWriter(filePathString);
            writer.write("[]");
            writer.close();
        }
        try (Reader reader = new FileReader(filePathString)) {

            // Convert JSON to JsonElement, and later to String
            JsonElement json = gson.fromJson(reader, JsonElement.class);
            String jsonInString = gson.toJson(json);
            User[] userArray = gson.fromJson(jsonInString, User[].class);
            List<HashMap<String, String>> items = new ArrayList<>();
            if (userArray != null) {
                for (User user : userArray) {
                    HashMap<String, String> item = new HashMap<>();
                    if (user != null) {
                        item.put("name", user.getName());
                        item.put("sets", user.getSets());
                        items.add(item);
                    }

                }
            }

            return items;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<HashMap<String, String>> getDomens() throws IOException {
        final String dir = System.getProperty("user.dir");
        String filePathString = dir + "\\preSets\\domens.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File f = new File(filePathString);
        File folder = new File(dir +
                File.separator + "preSets");
        if (!folder.exists()) {
            folder.mkdir();
        }
        if(!f.exists() || f.isDirectory()) {
            File newDir = new File(dir + "\\preSets");
            File myFile = new File(dir + "\\preSets\\domens.json");
            Writer writer = new FileWriter(filePathString);
            writer.write("[]");
            writer.close();
        }
        try (Reader reader = new FileReader(dir + "\\preSets\\domens.json")) {

            // Convert JSON to JsonElement, and later to String
            JsonElement json = gson.fromJson(reader, JsonElement.class);
            String jsonInString = gson.toJson(json);
            User[] userArray = gson.fromJson(jsonInString, User[].class);
            List<HashMap<String, String>> items = new ArrayList<>();
            if (userArray != null) {
                for (User user : userArray) {
                    HashMap<String, String> item = new HashMap<>();
                    item.put("name", user.getName());
                    items.add(item);
                }
            }

            return items;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void applyLiveDelete(String stringToEdit) throws SQLException {
        ArrayList<String> data = new ArrayList<>(Arrays.asList(stringToEdit.split("##")));
        ArrayList<String> column_names = getOnlineTableHeaders();
        data.remove(0);
        if (caller_transaction == 0) {
            stmt.executeUpdate("BEGIN WORK;");
            caller_transaction = caller_transaction + 1;
        }
        stmt.executeUpdate("SAVEPOINT savepoint" + changes_num + ";");
        for (String email : data) {
            stmt.executeUpdate("DELETE FROM " + mainDataBaseName + " WHERE email = " + quote(email) + ";");
        }
    }

    public static void applyLiveEdit(String stringToEdit) throws SQLException {
        if (stmt == null) {
            stmt = connection.createStatement();
        }
        if (caller_transaction == 0) {
            stmt.executeUpdate("BEGIN WORK;");
            caller_transaction = caller_transaction + 1;
        }
        ArrayList<ArrayList<String>> clearData = new ArrayList<ArrayList<String>>();
        ArrayList<String> listOfData = new ArrayList<>(Arrays.asList(stringToEdit.split("@@@")));
        for (String string : listOfData) {
            ArrayList<String> data = new ArrayList<>(Arrays.asList(string.split("##")));
            if (data.size() > 1) {
                data.remove(0);
                data.remove(0);
            }

            clearData.add(data);
        }
        clearData.remove(0);

        ArrayList<String> column_names = getOnlineTableHeaders();
        int j = 0;
        int i = 0;
        for (ArrayList<String> man : clearData) {
            String email = man.get(man.size() - 1);
            for (String ignored : column_names) {
                if (!column_names.get(j).equals("id")) {
                    stmt.executeUpdate("SAVEPOINT savepoint" + changes_num + ";");
                    stmt.executeUpdate("update jc_contact set " + column_names.get(j) + " = " + quote(man.get(i)) + " where email = " + quote(email) + ";");
                    if (i == man.size() - 1) {
                        break;
                    }
                    i++;
                }
                j++;

            }
            changes_num++;
        }


    }

    public static ArrayList<String> listFilesUsingDirectoryStream(String dir) throws IOException {
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

    public static void createExportDelete() throws IOException {
        final String dir = System.getProperty("user.dir");
        deleteAllFilesFolder(dir + "\\src\\main\\resources\\static\\assets\\export");
        System.out.println(listFilesUsingDirectoryStream(dir + "\\src\\main\\resources\\static\\assets\\export"));
    }

    public static void deleteAllFilesFolder(String path) {
        for (File myFile : new File(path).listFiles())
            if (myFile.isFile()) myFile.delete();
    }


}
