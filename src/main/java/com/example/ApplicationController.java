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
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static com.example.Constants.*;

@Controller
public class ApplicationController {
    private static final Logger logger = LogManager.getLogger(StartConnection.class);

    private final int numerOfTableLines = 20;
    private final StorageService storageService;
    private int startPosition = numerOfTableLines;
    private boolean flag = false;
    List<List<HashMap<String, String>>> NeededItems = new ArrayList<>();
    List<List<HashMap<String, String>>> ItemsToLoad = new ArrayList<>();
    List<List<HashMap<String, String>>> Pull = new ArrayList<>();
    List<List<HashMap<String, String>>> ActivePull = new ArrayList<>();
    List<List<HashMap<String, String>>> PullToShow = new ArrayList<>();

    @Autowired
    public ApplicationController(StorageService storageService) {
        this.storageService = storageService;
    }

    public static String uploadDirectory = System.getProperty("user.dir") + "/upload-dir";


    @GetMapping("/")
    public String home() throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        return "redirect:/file";
    }

    @GetMapping("/file")
    public String listUploadedFiles(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
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
            }


            List<HashMap<String, String>> tableHeaders = new ArrayList<>();

            List<String> headers = parser_excel.headers;
            if (headers == null) {
                headers = shrek.getOnlineTableHeaders();
            }

            if (headers != null) {
                HashMap<String, String> index = new HashMap<>();
                Boolean flag = false;
                for (String header : headers) {
                    if (header.equals("id")) flag = true;
                }
                if (!flag) {
                    index.put("header", "id");
                    tableHeaders.add(index);
                }

                for (int i = 0; i < headers.size(); i++) {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("header", headers.get(i));
                    tableHeaders.add(header);
                }
                if (headers != null && !headers.contains("comment")) {
                    headers.add("comment");

                }
            }

            logger.info("Data received");
            model.put("headers", tableHeaders);
            model.put("data", ActivePull);
            model.put("preSets", shrek.getPreSets());
            model.put("domens", shrek.getDomens());

        } catch (Exception e) {
            logger.error("Connection failed", e);
        }
        return "application";
    }

    @GetMapping("/sorting")
    public String sortin(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        ShrekBD shrek = new ShrekBD();
        model.put("preSets", shrek.getPreSets());
        model.put("items", NeededItems);
        return "sorting";
    }

    @GetMapping("/sorti")
    public String sorting(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        ShrekBD shrek = new ShrekBD();
        model.put("preSets", shrek.getPreSets());
        model.put("items", NeededItems);
        return "sorting";
    }

//
//    @PostMapping("/sorting")
//    public String handleDomen(@RequestParam("domen") String domen,
//                              RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
//        Class.forName("org.postgresql.Driver");
//        Connection connection = DriverManager.getConnection(url, user, password);
//        ShrekBD shrek = new ShrekBD();
//        List<HashMap<String, String>> items = new ArrayList<>();
//        NeededItems.clear();
//        items = shrek.getSortedListOfData();
//        for (int i = 0; i < items.size(); i++) {
//            if (items.get(i).get("email").contains(domen)) {
//                NeededItems.add(items.get(i));
//            }
//        }
//        return "redirect:/sorti";
//    }

    @PostMapping("/liveEdit")
    public String handleLiveEditing(@RequestParam("stringToEdit") String stringToEdit) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        ShrekBD.applyLiveEdit(stringToEdit);
        applyFrontLiveEdt(stringToEdit);
        return "redirect:/file";
    }

//    @PostMapping("/preSet")
//    public String handlePreSet(@RequestParam("chosenPreSet") String chosenPreSet,
//                               RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
//        Class.forName("org.postgresql.Driver");
//        Connection connection = DriverManager.getConnection(url, user, password);
//        ShrekBD shrek = new ShrekBD();
//        List<HashMap<String, String>> items = new ArrayList<>();
//        NeededItems.clear();
//        String domens = "";
//        items = shrek.getSortedListOfData();
//        List<HashMap<String, String>> preSets = shrek.getPreSets();
//        for (HashMap dict : preSets) {
//            if (dict.get("name").equals(chosenPreSet)) {
//                domens = (String) dict.get("sets");
//            }
//        }
//        for (HashMap dict : items) {
//            for (String domen : domens.split(" ")) {
////                if (dict.get("email").toString().contains(domen)) {
////                    NeededItems.add(dict);
////                }
//                if (dict.get("email").toString().substring(dict.get("email").toString().indexOf("@") + 1).contains(domen)) {
//                    NeededItems.add(dict);
//                }
//            }
//        }
//        return "redirect:/file";
//    }

//    @PostMapping("/addPreSet")
//    public String addPreSet(@RequestParam("name") String name, @RequestParam("sets") String sets, RedirectAttributes redirectAttributes) throws FileNotFoundException {
//        final String dir = System.getProperty("user.dir");
//        String userJson = new Scanner(new File(dir + "\\preSets\\staff.json")).useDelimiter("\\Z").next();
//
//        Gson gson = new Gson();
//
//        User[] userArray = gson.fromJson(userJson, User[].class);
//        User[] newUserArray = new User[userArray.length + 1];
//        for (int i = 0; i < userArray.length; i++) {
//            newUserArray[i] = userArray[i];
//        }
//        User newUser = new User();
//        newUser.setName(name);
//        newUser.setSets(sets);
//        newUserArray[newUserArray.length - 1] = newUser;
//        String json = gson.toJson(newUserArray);
//        try (FileWriter writer = new FileWriter(dir + "\\preSets\\staff.json")) {
//            writer.write(json);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "redirect:/sorti";
//    }

//    @PostMapping("/find")
//    public String handleKey(@RequestParam("key") String key,
//                            RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
//        Class.forName("org.postgresql.Driver");
//        Connection connection = DriverManager.getConnection(url, user, password);
//        ShrekBD shrek = new ShrekBD();
//        List<HashMap<String, String>> items = new ArrayList<>();
//        NeededItems.clear();
//        items = shrek.getSortedListOfData();
//        for (int i = 0; i < items.size(); i++) {
////            if (items.get(i).get("name").contains(key) || items.get(i).get("age").contains(key) || items.get(i).get("phone").contains(key) || items.get(i).get("email").contains(key)) {
//            if (items.get(i).get("name").contains(key) || items.get(i).get("age").contains(key) || items.get(i).get("phone").contains(key)) {
//                NeededItems.add(items.get(i));
//            }
//        }
//        return "redirect:/sorti";
//    }

    @GetMapping("/us")
    public String main(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        model.put("items", shrek.getSortedListOfData());
        return "uploadFormUser";
    }

    @GetMapping("/loginError")
    public String loginError() throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        return "loginError";
    }

    @GetMapping("/login")
    public String login(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        return "login";
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        FileUtils.cleanDirectory(new File(uploadDirectory));
        storageService.store(file);
        StartConnection.start();
        return "redirect:/file";
    }


    @PostMapping("/drop")
    public String handleFileUpload() throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        shrek.drop();
        return "redirect:/file";
    }


    @PostMapping("/export")
    public String handlePath(@RequestParam("path") String path,
                             RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        shrek.export(path);

        return "redirect:/file";
    }

//    @PostMapping("/exportPreSet")
//    public String exportPreSet(@RequestParam("path") String path,
//                               RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
//        Class.forName("org.postgresql.Driver");
//        Connection connection = DriverManager.getConnection(url, user, password);
//        ShrekBD shrek = new ShrekBD();
//       List<HashMap<String, String>> NeededItems = new ArrayList<>();
//        List<String> listOfEmails = new ArrayList<String>();
//        for (HashMap dict : NeededItems) {
//            listOfEmails.add(dict.get("email").toString());
//        }
//        shrek.exportPreSet(path, listOfEmails);
//
//        return "redirect:/sorti";
//    }

    @PostMapping("/log")
    public String handleLogo(@RequestParam("logo") String logo, @RequestParam("passwd") String passwd) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {

//        if (logo.equals("admin@admin") && passwd.equals("admin")) {
//            return "redirect:/file";
//        } else if (logo.equals("user@user") && passwd.equals("user")) {
//            return "redirect:/us";
//        } else {
//            return "redirect:/loginError";
//        }
//        if (logo.equals("admin")) {
//            return "redirect:/file";
//        } else if (logo.equals("user")) {
//            return "redirect:/us";
//        } else {
//            return "redirect:/loginError";
//        }
        return "redirect:/file";


    }

    @PostMapping("/changeView")
    public String moveView(@RequestParam("direction") String direction) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
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


        return "redirect:/file";

    }

    @PostMapping("/addPreSet")
    public String addPreSet(@RequestParam("name") String name) throws FileNotFoundException {
        String sets = "";
        final String dir = System.getProperty("user.dir");
        String userJson = new Scanner(new File(dir + "\\preSets\\staff.json")).useDelimiter("\\Z").next();

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
        try (FileWriter writer = new FileWriter(dir + "\\preSets\\staff.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/addDomen")
    public String addDomen(@RequestParam("name") String name) throws FileNotFoundException {
        String sets = "";
        final String dir = System.getProperty("user.dir");
        String userJson = new Scanner(new File(dir + "\\preSets\\domens.json")).useDelimiter("\\Z").next();

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
        try (FileWriter writer = new FileWriter(dir + "\\preSets\\domens.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/deleteDomen")
    public String deleteDomen(@RequestParam("domens") String name) throws FileNotFoundException {
        String sets = "";
        final String dir = System.getProperty("user.dir");
        String userJson = new Scanner(new File(dir + "\\preSets\\domens.json")).useDelimiter("\\Z").next();

        Gson gson = new Gson();

        User[] userArray = gson.fromJson(userJson, User[].class);
        User[] newUserArray = new User[userArray.length - 1];
        int counter = 0;
        for (int i = 0; i < userArray.length; i++) {
            if (!name.replaceAll("[<>]*", "").equals(userArray[i].getName())) {
                newUserArray[counter] = userArray[i];
                counter++;
            }
        }
        String json = gson.toJson(newUserArray);
        try (FileWriter writer = new FileWriter(dir + "\\preSets\\domens.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/deletePreset")
    public String deletePreset(@RequestParam("presets") String name) throws FileNotFoundException {
        String sets = "";
        final String dir = System.getProperty("user.dir");
        String userJson = new Scanner(new File(dir + "\\preSets\\staff.json")).useDelimiter("\\Z").next();

        Gson gson = new Gson();

        User[] userArray = gson.fromJson(userJson, User[].class);
        User[] newUserArray = new User[userArray.length - 1];
        int counter = 0;
        for (int i = 0; i < userArray.length; i++) {
            if (!name.replaceAll("[<>]*", "").equals(userArray[i].getName())) {
                newUserArray[counter] = userArray[i];
                counter++;
            }
        }
        String json = gson.toJson(newUserArray);
        try (FileWriter writer = new FileWriter(dir + "\\preSets\\staff.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    @PostMapping("/add-form")
    public String addForm() {
        return "redirect:/file";
    }

    @PostMapping("/usePreSet")
    public String handlePreSet(@RequestParam("used") String used) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
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
                    if (dict.get("Data").substring(dict.get("Data").indexOf("@") + 1).contains(domen)) {
                        NeededItems.add(list);
                    }
                }
            }
        }
        Pull = NeededItems;
        ActivePull.clear();
        return "redirect:/file";

    }

    @PostMapping("/getData")
    public String addData(@RequestParam("preSets") String preSets, @RequestParam("domens") String domens) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();

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
        try (Reader reader = new FileReader(dir + "\\preSets\\staff.json")) {

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
            try (FileWriter writer = new FileWriter(dir + "\\preSets\\staff.json")) {
                writer.write(jsonToWrite);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/file";

    }

    public void applyFrontLiveEdt(String dataToEdit) {
        ArrayList<String> data = new ArrayList<>(Arrays.asList(dataToEdit.split("##")));
        String idToEdit = data.get(0);
        List<HashMap<String, String>> finded = new ArrayList<>();
        List<HashMap<String, String>> newDataRow = new ArrayList<>();
        for (List<HashMap<String, String>> dataRow : ActivePull) {
            if (dataRow.get(0).get("Data").equals(idToEdit)) {
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
    }

}

