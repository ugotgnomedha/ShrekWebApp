package com.example;

import com.example.storage.StorageService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.Constants.*;

@Controller
public class ApplicationController {

    private static final Logger logger = LogManager.getLogger(ApplicationController.class);
    public static String uploadDirectory = System.getProperty("user.dir") + "/upload-dir";
    private final int numerOfTableLines = 20;
    public static Integer counter = 1;
    public static Boolean direction = true;
    private final StorageService storageService;
    private int startPosition = numerOfTableLines;
    private boolean flag = false;
    List<List<HashMap<String, String>>> NeededItems = new ArrayList<>();
    List<List<HashMap<String, String>>> ItemsToLoad = new ArrayList<>();
    List<List<HashMap<String, String>>> PresettedData = new ArrayList<>();
    List<List<HashMap<String, String>>> Pull = new ArrayList<>();
    List<List<HashMap<String, String>>> ActivePull = new ArrayList<>();
    List<List<HashMap<String, String>>> PullToShow = new ArrayList<>();
    List<List<String>> history = new ArrayList<>();

    @Autowired
    public ApplicationController(StorageService storageService) {
        this.storageService = storageService;
    }


    @GetMapping("/")
    public String home() throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        return "redirect:/file";
    }

    @GetMapping("/file")
    public String listUploadedFiles(Map<String, Object> model) {
        try {
            Class.forName("org.postgresql.Driver");
            ShrekBD shrek = new ShrekBD();
            List<HashMap<String, String>> ItemsToLoadOn = new ArrayList<>();

            if (Pull.isEmpty()) {
                Pull = shrek.getSortedListOfDataImpact();
            }
            if (ActivePull.isEmpty()) {
                if (numerOfTableLines < Pull.size()) {
                    for (int i = 0; i < numerOfTableLines; i++) {
                        ActivePull.add(Pull.get(i));
                    }
                } else {
                    ActivePull = Pull;
                }

                PresettedData = Pull;
            }
            List<HashMap<String, String>> tableHeaders = new ArrayList<>();

            List<String> headers = ExcelParser.excelheaders;
            if (headers == null) {
                headers = shrek.getOnlineTableHeaders();
            }

            if (headers != null) {
                for (int i = 0; i < headers.size(); i++) {
                    if (!headers.get(i).equals("id")) {
                        HashMap<String, String> header = new HashMap<>();
                        if (headers.get(i).equals("comment")) {
                            header.put("header", "Комментарий");
                        } else {
                            header.put("header", headers.get(i));
                        }

                        tableHeaders.add(header);
                    }

                }

            }
            ArrayList<Domen> statistics = new ArrayList<>();
            HashMap<String, Integer> mappedStatistic = ExcelDataInserter.domianFunc();
            if (mappedStatistic != null) {
                for (String key : mappedStatistic.keySet()) {
                    statistics.add(new Domen(key, mappedStatistic.get(key)));
                }
            }
            logger.info("Data loaded to frontend");
            model.put("statistics", statistics);
            model.put("headers", tableHeaders);
            model.put("data", ActivePull);
            model.put("preSets", shrek.getPreSets());
            model.put("domens", shrek.getDomens());

        } catch (Exception e) {
            logger.error("Failed to load data to frontend", e);
            e.printStackTrace();
        }
        return "application";
    }

    @GetMapping("/fileDownload")
    public void giveFile(HttpServletResponse response) {
        try {
            final String dir = System.getProperty("user.dir");
            try {
                FileWriter writer = new FileWriter(dir + "/files/data.csv");
                for (List<HashMap<String, String>> row : PresettedData) {
                    List<String> clearList = new ArrayList<>();
                    for (HashMap<String, String> dataCell : row) {
                        clearList.add(dataCell.get("Data"));
                    }
                    String listString = String.join(", ", clearList);
                    writer.write(listString);
                    writer.write("\n");
                }

                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File file = new File(dir + "/files/data.csv");

            response.setContentType("application/csv");

            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String currentDateTime = dateFormatter.format(new Date());


            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=data_" + currentDateTime + ".csv";
            response.setHeader(headerKey, headerValue);
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            byte[] buffer = new byte[8192];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @PostMapping("/liveEdit")
    public String handleLiveEditing(@RequestParam("stringToEdit") String stringToEdit) {
        try {
            createCheckPoint(Boolean.TRUE);
            Class.forName("org.postgresql.Driver");
            ShrekBD shrek = new ShrekBD();
            ShrekBD.applyLiveEdit(stringToEdit);
            applyFrontLiveEdt(stringToEdit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";
    }

    @PostMapping("/liveDelete")
    public String handleLiveDeleting(@RequestParam("emailsToDelete") String emailsToDelete) throws ClassNotFoundException, SQLException {
        try {
            createCheckPoint(Boolean.TRUE);
            Class.forName("org.postgresql.Driver");
            ShrekBD shrek = new ShrekBD();
            ShrekBD.applyLiveDelete(emailsToDelete);
            applyFrontLiveDelete(emailsToDelete);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/file";
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
            FileUtils.cleanDirectory(new File(uploadDirectory));
            storageService.store(file);
            DBConnect.connect();
            Pull.clear();
            ActivePull.clear();
            PresettedData.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";
    }

    @PostMapping("/changeView")
    public String moveView(@RequestParam("direction") String direction) {
        try {
            Class.forName("org.postgresql.Driver");
            ShrekBD shrek = new ShrekBD();

            ActivePull.clear();

            Boolean movementRight = true;
            if (direction.equals("right")) {
                if (movementRight) {
                    if (Pull.size() > numerOfTableLines) {
                        for (int i = startPosition; i < startPosition + numerOfTableLines; i++) {
                            ActivePull.add(Pull.get(i));
                        }
                        if (startPosition + numerOfTableLines < Pull.size()) {
                            startPosition += numerOfTableLines;
                        }
                    }

                } else {
                    movementRight = true;
                    startPosition += numerOfTableLines;

                    if (Pull.size() > numerOfTableLines) {
                        for (int i = startPosition; i < startPosition + numerOfTableLines; i++) {
                            ActivePull.add(Pull.get(i));
                        }
                    } else {
                        ActivePull = Pull;
                    }
                    if (startPosition + numerOfTableLines < Pull.size()) {
                        startPosition += numerOfTableLines;
                    }
                }

            } else if (direction.equals("left")) {
                if (!movementRight) {
                    for (int i = startPosition - numerOfTableLines; i < startPosition; i++) {
                        ActivePull.add(Pull.get(i));
                    }
                    if (startPosition - numerOfTableLines > 0) {
                        startPosition -= numerOfTableLines;
                    }

                } else {
                    movementRight = false;
                    if (startPosition - numerOfTableLines > 0) {
                        startPosition -= numerOfTableLines;
                    }
                    for (int i = startPosition - numerOfTableLines; i < startPosition; i++) {
                        ActivePull.add(Pull.get(i));
                    }
                    if (startPosition - numerOfTableLines > 0) {
                        startPosition -= numerOfTableLines;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/save")
    public String save() throws SQLException {
        try {
            ShrekBD shrek = new ShrekBD();
            shrek.save();
            logger.info("Data send to online");
        } catch (Exception e) {
            logger.error("Failed to send data to online", e);
        }

        return "redirect:/file";
    }

    @PostMapping("/addPreSet")
    public String addPreSet(@RequestParam("name") String name) throws FileNotFoundException {
        try {
            createCheckPoint(Boolean.FALSE);
            String sets = "";
            final String dir = System.getProperty("user.dir");
            String userJson = new Scanner(new File(dir + "/preSets/staff.json")).useDelimiter("\\Z").next();

            Gson gson = new Gson();

            User[] userArray = gson.fromJson(userJson, User[].class);
            User[] newUserArray = new User[userArray.length + 1];
            for (int i = 0; i < userArray.length; i++) {
                newUserArray[i] = userArray[i];
            }
            User newUser = new User();
            newUser.setName(name);
            newUser.setSets(sets);
            newUserArray[newUserArray.length - 1] = newUser;
            String json = gson.toJson(newUserArray);
            try (FileWriter writer = new FileWriter(dir + "/preSets/staff.json")) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/addDomen")
    public String addDomen(@RequestParam("name") String name) throws FileNotFoundException {
        try {
            createCheckPoint(Boolean.FALSE);
            String sets = "";
            final String dir = System.getProperty("user.dir");
            String userJson = new Scanner(new File(dir + "/preSets/domens.json")).useDelimiter("\\Z").next();

            Gson gson = new Gson();

            User[] userArray = gson.fromJson(userJson, User[].class);
            User[] newUserArray = new User[userArray.length + 1];
            for (int i = 0; i < userArray.length; i++) {
                newUserArray[i] = userArray[i];
            }
            User newUser = new User();
            newUser.setName(name);
            newUser.setSets(sets);
            newUserArray[newUserArray.length - 1] = newUser;
            String json = gson.toJson(newUserArray);
            try (FileWriter writer = new FileWriter(dir + "/preSets/domens.json")) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/deleteDomen")
    public String deleteDomen(@RequestParam("domens") String name) throws FileNotFoundException {
        try {
            String[] words = name.trim().split(" ");
            ArrayList<String> keys = new ArrayList<>(Arrays.asList(words));
            createCheckPoint(Boolean.FALSE);

            String sets = "";
            final String dir = System.getProperty("user.dir");
            String userJson = new Scanner(new File(dir + "/preSets/domens.json")).useDelimiter("\\Z").next();

            Gson gson = new Gson();

            User[] userArray = gson.fromJson(userJson, User[].class);
            ArrayList<User> userA = new ArrayList<>(Arrays.asList(userArray));
            ArrayList<User> newUserA = new ArrayList<>(userA);
            if (keys.get(0).equals("on")) {
                newUserA.clear();
            } else {
                for (User user : userA) {
                    for (String key : keys) {
                        if (user.getName().equals(key)) {
                            newUserA.remove(user);
                        }
                    }
                }

            }
            User[] newUserArray = newUserA.toArray(new User[newUserA.size()]);
            String json = gson.toJson(newUserArray);
            try (FileWriter writer = new FileWriter(dir + "/preSets/domens.json")) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/deletePreset")
    public String deletePreset(@RequestParam("presets") String name) throws FileNotFoundException {
        try {
            String[] words = name.trim().split(" ");
            ArrayList<String> keys = new ArrayList<>(Arrays.asList(words));
            createCheckPoint(Boolean.FALSE);

            String sets = "";
            final String dir = System.getProperty("user.dir");
            String userJson = new Scanner(new File(dir + "/preSets/staff.json")).useDelimiter("\\Z").next();

            Gson gson = new Gson();

            User[] userArray = gson.fromJson(userJson, User[].class);
            ArrayList<User> userA = new ArrayList<>(Arrays.asList(userArray));
            ArrayList<User> newUserA = new ArrayList<>(userA);
            if (keys.get(0).equals("on")) {
                newUserA.clear();
            } else {
                for (User user : userA) {
                    for (String key : keys) {
                        if (user.getName().equals(key)) {
                            newUserA.remove(user);
                        }
                    }
                }

            }
            User[] newUserArray = newUserA.toArray(new User[newUserA.size()]);
            String json = gson.toJson(newUserArray);
            try (FileWriter writer = new FileWriter(dir + "/preSets/staff.json")) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/add-form")
    public String addForm() {
        return "redirect:/file";
    }

    @PostMapping("/cancel-form")
    public String cancelForm() throws SQLException {
        try {
            cancelPresetsAndDomens();
            Pull = new ArrayList<>();
            ActivePull = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";
    }

    public void cancelLiveEdit() throws SQLException {
        try {
            ShrekBD shrek = new ShrekBD();
            Statement stmt = ShrekBD.stmt;
            int index = ShrekBD.changes_num - counter;
            if (direction) {
                if (ShrekBD.changes_num - counter > 0) {
                    //index = index - 1;
                    index--;
                    stmt.executeUpdate("ROLLBACK TO savepoint" + index + "");
                    counter = counter + 1;
                }
                direction = false;
            } else {
                if (ShrekBD.changes_num - counter >= 0) {
                    //index = index - 1;
                    stmt.executeUpdate("ROLLBACK TO savepoint" + index + "");
                    counter = counter + 1;
                }
                direction = false;
            }
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/EasyExportDelete")
    public String easyExportDelete(@RequestParam("null") String neededArgument) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        try {
            Class.forName("org.postgresql.Driver");
            ShrekBD shrek = new ShrekBD();
            shrek.createExportDelete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/usePreSet")
    public String handlePreSet(@RequestParam("used") String used) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        try {
            if (used.length() > 2) {
                Class.forName("org.postgresql.Driver");
                ShrekBD shrek = new ShrekBD();
                List<List<HashMap<String, String>>> items = shrek.getSortedListOfDataImpact();
                NeededItems.clear();
                String domens = "";
                List<String> preSetsInForm = new ArrayList<>();
                List<HashMap<String, String>> preSets = shrek.getPreSets();
                for (String preSet : used.split(",")) {
                    preSet = preSet.replaceAll("[<>]*", "");
                    preSetsInForm.add(preSet);
                    for (HashMap dict : preSets) {
                        if (dict.get("name").equals(preSet)) {
                            domens += (String) dict.get("sets");
                        }
                    }

                }
                for (List<HashMap<String, String>> list : items) {
                    for (HashMap<String, String> dict : list) {
                        for (String domen : domens.split(" ")) {
                            if (!domen.contains("@")) {
                                if (dict.get("Data").substring(dict.get("Data").indexOf("@") + 1).contains(domen)) {
                                    NeededItems.add(list);
                                }
                            } else {
                                if (dict.get("Data").equals(domen)) {
                                    NeededItems.add(list);
                                }
                            }

                        }
                    }
                }
                Pull = NeededItems;
                PresettedData = NeededItems;
                ActivePull.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/addUserToPresetLive")
    public String addUserToPresetLive(@RequestParam("preSets") String preSets, @RequestParam("domens") String domens) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        try {
            createCheckPoint(Boolean.FALSE);
            Class.forName("org.postgresql.Driver");

            List<String> domenList = new ArrayList<>();
            for (String domen : domens.split("##")) {
                domen = domen.replaceAll("[<>]*", "");
                domenList.add(domen);
            }
            domenList.remove(0);

            List<String> preSetsInForm = new ArrayList<>();

            for (String preSet : preSets.split("!")) {
                preSet = preSet.replaceAll("[<>]*", "");
                preSetsInForm.add(preSet);

            }
            final String dir = System.getProperty("user.dir");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (Reader reader = new FileReader(dir + "/preSets/staff.json")) {

                // Convert JSON to JsonElement, and later to String
                JsonElement json = gson.fromJson(reader, JsonElement.class);
                String jsonInString = gson.toJson(json);
                User[] userArray = gson.fromJson(jsonInString, User[].class);
                for (User user : userArray) {
                    for (String preSet : preSetsInForm) {
                        if (user.getName().equals(preSet)) {
                            for (String domen : domenList) {
                                if (user.getSets().equals("")) {
                                    user.setSets(domen);
                                } else {
                                    user.setSets(user.getSets() + " " + domen);
                                }
                            }
                        }
                    }
                }

                String jsonToWrite = gson.toJson(userArray);
                try (FileWriter writer = new FileWriter(dir + "/preSets/staff.json")) {
                    writer.write(jsonToWrite);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }


    @PostMapping("/getData")
    public String addData(@RequestParam("preSets") String preSets, @RequestParam("domens") String domens) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        try {
            createCheckPoint(Boolean.FALSE);
            Class.forName("org.postgresql.Driver");

            List<String> domenList = new ArrayList<>();
            for (String domen : domens.split("!")) {
                domen = domen.replaceAll("[<>]*", "");
                domenList.add(domen);
            }

            List<String> preSetsInForm = new ArrayList<>();

            for (String preSet : preSets.split("!")) {
                preSet = preSet.replaceAll("[<>]*", "");
                preSetsInForm.add(preSet);

            }
            final String dir = System.getProperty("user.dir");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (Reader reader = new FileReader(dir + "/preSets/staff.json")) {

                JsonElement json = gson.fromJson(reader, JsonElement.class);
                String jsonInString = gson.toJson(json);
                User[] userArray = gson.fromJson(jsonInString, User[].class);
                for (User user : userArray) {
                    for (String preSet : preSetsInForm) {
                        if (user.getName().equals(preSet)) {
                            for (String domen : domenList) {
                                if (user.getSets().equals("")) {
                                    user.setSets(domen);
                                } else {
                                    user.setSets(user.getSets() + " " + domen);
                                }
                            }
                        }
                    }
                }

                String jsonToWrite = gson.toJson(userArray);
                try (FileWriter writer = new FileWriter(dir + "/preSets/staff.json")) {
                    writer.write(jsonToWrite);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    public void applyFrontLiveEdt(String dataToEdit) {
        try {
            ArrayList<String> fullData = new ArrayList<>(Arrays.asList(dataToEdit.split("@@@")));
            fullData.remove(0);
            for (String string : fullData) {
                ArrayList<String> data = new ArrayList<>(Arrays.asList(string.split("##")));
                data.remove(0);
                data.remove(0);
                String emailToEdit = data.get(data.size() - 1);
                List<HashMap<String, String>> finded = new ArrayList<>();
                List<HashMap<String, String>> newDataRow = new ArrayList<>();
                for (List<HashMap<String, String>> dataRow : ActivePull) {
                    if (dataRow.get(dataRow.size() - 1).get("Data").equals(emailToEdit)) {
                        for (String dataCell : data) {
                            HashMap<String, String> d = new HashMap<>();
                            d.put("Data", dataCell);
                            newDataRow.add(d);
                        }
                        finded = dataRow;
                        break;
                    }
                }
                ActivePull.set(ActivePull.indexOf(finded), newDataRow);
                PresettedData.set(PresettedData.indexOf(finded), newDataRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void applyFrontLiveDelete(String emailsToDelete) {
        try {
            ArrayList<String> data = new ArrayList<>(Arrays.asList(emailsToDelete.split("##")));
            List<List<HashMap<String, String>>> newActivePull = new ArrayList<>();
            List<List<HashMap<String, String>>> newPresettedData = new ArrayList<>();
            data.remove(0);
            for (List<HashMap<String, String>> dataRow : ActivePull) {
                if (!data.contains(dataRow.get(dataRow.size() - 1).get("Data"))) {
                    newActivePull.add(dataRow);
                }
            }
            for (List<HashMap<String, String>> dataRow : PresettedData) {
                if (!data.contains(dataRow.get(dataRow.size() - 1).get("Data"))) {
                    newPresettedData.add(dataRow);
                }
            }
            ActivePull = newActivePull;
            PresettedData = newPresettedData;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelPresetsAndDomens() throws SQLException {
        try {
            if (history.size() > 0) {
                final String dir = System.getProperty("user.dir");
                try (FileWriter writer = new FileWriter(dir + "/preSets/domens.json", false)) {
                    // запись всей строки
                    if (history.size() > 0) {
                        String text = history.get(history.size() - 1).get(0);
                        if (text.equals("[]")) {
                            text = "[]";
                        }
                        writer.write(text);
                    } else {
                        writer.write("[]");
                    }

                    writer.flush();

                } catch (IOException ex) {

                    ex.printStackTrace();
                }
                try (FileWriter writer = new FileWriter(dir + "/preSets/staff.json", false)) {
                    // запись всей строки
                    if (history.size() > 0) {
                        String text = history.get(history.size() - 1).get(1);
                        if (text.equals("[]")) {
                            text = "[]";
                        }
                        writer.write(text);
                    } else {
                        writer.write("[]");
                    }

                    writer.flush();

                } catch (IOException ex) {

                    ex.printStackTrace();
                }
                if (history.size() > 0) {
                    cancelLiveEdit();
                }
                if (history.size() > 0) history.remove(history.size() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void createCheckPoint(Boolean liveEdit) {
        try {
            final String dir = System.getProperty("user.dir");
            String resultOfReading = "";
            try (FileReader reader = new FileReader(dir + "/preSets/domens.json")) {
                // читаем посимвольно
                int c;
                while ((c = reader.read()) != -1) {

                    resultOfReading += (char) c;
                }
            } catch (IOException ex) {

                ex.printStackTrace();
            }
            List<String> data = new ArrayList<>();
            data.add(resultOfReading);
            resultOfReading = "";
            try (FileReader reader = new FileReader(dir + "/preSets/staff.json")) {
                // читаем посимвольно
                int c;
                while ((c = reader.read()) != -1) {

                    resultOfReading += (char) c;
                }
            } catch (IOException ex) {

                ex.printStackTrace();
            }
            data.add(resultOfReading);
            if (liveEdit) {
                data.add("RegisteredLiveEdit");
            } else data.add("LiveEditNotRegistered");

            history.add(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
