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

    public static Statement stmt = null;
    public static Connection connection = null;
    public static Integer changes_num = 0;
    public static Integer caller_transaction = 0;

    public static void tempBeginConnection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            stmt = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void save() {
        try {
            Statement stat = connection.createStatement();
            stat.executeUpdate("COMMIT WORK;");
            stat.close();
            caller_transaction = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = null;
        try {
            convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convFile;
    }


    public List<List<HashMap<String, String>>> getSortedListOfDataImpact() {
        List<List<HashMap<String, String>>> items = null;
        List<String> emailSynonyms = new ArrayList<>();
        emailSynonyms.add("email");
        emailSynonyms.add("почта");
        emailSynonyms.add("почтовый ящик");
        emailSynonyms.add("e-mail");
        String emailName = "";
        try {
            Statement stat = connection.createStatement();
            items = new ArrayList<>();
            List<String> headers = ExcelParser.excelheaders;
            if (headers == null || headers.size() == 0) {
                headers = getOnlineTableHeaders();
                for (String header : headers) {
                    if (emailSynonyms.contains(header.toLowerCase())) {
                        emailName = header;
                        break;
                    }
                }
            } else {
                emailName = ExcelParser.emailNameFromExcel;
            }
            if (headers.size() > 0) {
                String queryString = "select * from " + mainDataBaseName + " ORDER BY " + emailName + " ASC;";
                ResultSet rs = stat.executeQuery(queryString);
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
            }
            stat.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public static ArrayList<String> getOnlineTableHeaders() {
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

        return headers;
    }

    public static String quote(String s) {
        return new StringBuilder()
                .append('\'')
                .append(s)
                .append('\'')
                .toString();
    }

    public List<HashMap<String, String>> getPreSets() {
        try {
            final String dir = System.getProperty("user.dir");
            String filePathString = dir + "/preSets/staff.json";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File f = new File(filePathString);
            File folder = new File(dir +
                    File.separator + "preSets");
            if (!folder.exists()) {
                folder.mkdir();
            }
            if (!f.exists() || f.isDirectory()) {
                File newDir = new File(dir + "/preSets");
                File myFile = new File(dir + "/preSets/staff.json");
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<HashMap<String, String>> getDomens() {
        try {
            final String dir = System.getProperty("user.dir");
            String filePathString = dir + "/preSets/domens.json";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File f = new File(filePathString);
            File folder = new File(dir +
                    File.separator + "preSets");
            if (!folder.exists()) {
                folder.mkdir();
            }
            if (!f.exists() || f.isDirectory()) {
                File newDir = new File(dir + "/preSets");
                File myFile = new File(dir + "/preSets/domens.json");
                Writer writer = new FileWriter(filePathString);
                writer.write("[]");
                writer.close();
            }
            try (Reader reader = new FileReader(dir + "/preSets/domens.json")) {

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void applyLiveDelete(String stringToEdit) {
        try {
            ArrayList<String> data = new ArrayList<>(Arrays.asList(stringToEdit.split("##")));
            data.remove(0);
            if (caller_transaction == 0) {
                stmt.executeUpdate("BEGIN WORK;");
                caller_transaction = caller_transaction + 1;
            }
            stmt.executeUpdate("SAVEPOINT savepoint" + changes_num + ";");
            for (String email : data) {
                stmt.executeUpdate("DELETE FROM " + mainDataBaseName + " WHERE email = " + quote(email) + ";");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void applyLiveEdit(String stringToEdit) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static ArrayList<String> listFilesUsingDirectoryStream(String dir) throws IOException {
        ArrayList<String> fileList = null;
        try {
            fileList = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
                for (Path path : stream) {
                    if (!Files.isDirectory(path)) {
                        fileList.add(path.getFileName()
                                .toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileList;
    }

    public static void createExportDelete() {
        try {
            final String dir = System.getProperty("user.dir");
            deleteAllFilesFolder(dir + "\\src\\main\\resources\\static\\assets\\export");
            System.out.println(listFilesUsingDirectoryStream(dir + "\\src\\main\\resources\\static\\assets\\export"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deleteAllFilesFolder(String path) {
        try {
            for (File myFile : Objects.requireNonNull(new File(path).listFiles()))
                if (myFile.isFile()) myFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
