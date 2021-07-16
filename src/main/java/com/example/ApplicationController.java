package com.example;

import com.example.storage.StorageService;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static com.example.Constants.*;

@Controller
public class ApplicationController {

    private final int numerOfTableLines = 250;
    private final StorageService storageService;
    private int startPosition = numerOfTableLines;
    private boolean flag = false;

    @Autowired
    public ApplicationController(StorageService storageService) {
        this.storageService = storageService;
    }

    public static String uploadDirectory = System.getProperty("user.dir") + "/upload-dir";
    List<HashMap<String, String>> NeededItems = new ArrayList<>();

    @GetMapping("/")
    public String home() throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        return "login";
    }

    @GetMapping("/file")
    public String listUploadedFiles(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        if (NeededItems.isEmpty()) {
            List<HashMap<String, String>> allData = shrek.getListOfData();
            if (!allData.isEmpty()) {
                if (numerOfTableLines < allData.size()) {
                    for (int i = 0; i < numerOfTableLines; i++) {
                        NeededItems.add(allData.get(i));
                    }
                } else {
                    NeededItems = allData;
                }

            }

        }
        model.put("items", NeededItems);
        model.put("preSets", shrek.getPreSets());
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


    @PostMapping("/sorting")
    public String handleDomen(@RequestParam("domen") String domen,
                              RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        List<HashMap<String, String>> items = new ArrayList<>();
        NeededItems.clear();
        items = shrek.getListOfData();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).get("email").contains(domen)) {
                NeededItems.add(items.get(i));
            }
        }
        return "redirect:/sorti";
    }

    @PostMapping("/liveEdit")
    public String handleLiveEditing(@RequestParam("key") String key, @RequestParam("name") String name, @RequestParam("sex") String sex, @RequestParam("age") String age, @RequestParam("phone") String phone, @RequestParam("email") String email, @RequestParam("comment") String comment) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        ShrekBD.applyLiveEdit(key, name, sex, age, phone, email, comment);
        return "redirect:/file";
    }

    @PostMapping("/preSet")
    public String handlePreSet(@RequestParam("chosenPreSet") String chosenPreSet,
                               RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        List<HashMap<String, String>> items = new ArrayList<>();
        NeededItems.clear();
        String domens = "";
        items = shrek.getListOfData();
        List<HashMap<String, String>> preSets = shrek.getPreSets();
        for (HashMap dict : preSets) {
            if (dict.get("name").equals(chosenPreSet)) {
                domens = (String) dict.get("sets");
            }
        }
        for (HashMap dict : items) {
            for (String domen : domens.split(" ")) {
//                if (dict.get("email").toString().contains(domen)) {
//                    NeededItems.add(dict);
//                }
                if (dict.get("email").toString().substring(dict.get("email").toString().indexOf("@") + 1).contains(domen)) {
                    NeededItems.add(dict);
                }
            }
        }
        return "redirect:/file";
    }

    @PostMapping("/addPreSet")
    public String addPreSet(@RequestParam("name") String name, @RequestParam("sets") String sets, RedirectAttributes redirectAttributes) throws FileNotFoundException {
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
        return "redirect:/sorti";
    }

    @PostMapping("/find")
    public String handleKey(@RequestParam("key") String key,
                            RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        List<HashMap<String, String>> items = new ArrayList<>();
        NeededItems.clear();
        items = shrek.getListOfData();
        for (int i = 0; i < items.size(); i++) {
//            if (items.get(i).get("name").contains(key) || items.get(i).get("age").contains(key) || items.get(i).get("phone").contains(key) || items.get(i).get("email").contains(key)) {
            if (items.get(i).get("name").contains(key) || items.get(i).get("age").contains(key) || items.get(i).get("phone").contains(key)) {
                NeededItems.add(items.get(i));
            }
        }
        return "redirect:/sorti";
    }

    @GetMapping("/us")
    public String main(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        model.put("items", shrek.getListOfData());
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

    @PostMapping("/exportPreSet")
    public String exportPreSet(@RequestParam("path") String path,
                               RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
//        List<HashMap<String, String>> NeededItems = new ArrayList<>();
        List<String> listOfEmails = new ArrayList<String>();
        for (HashMap dict : NeededItems) {
            listOfEmails.add(dict.get("email").toString());
        }
        shrek.exportPreSet(path, listOfEmails);

        return "redirect:/sorti";
    }

    @PostMapping("/log")
    public String handleLogo(@RequestParam("logo") String logo, @RequestParam("passwd") String passwd) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {

//        if (logo.equals("admin@admin") && passwd.equals("admin")) {
//            return "redirect:/file";
//        } else if (logo.equals("user@user") && passwd.equals("user")) {
//            return "redirect:/us";
//        } else {
//            return "redirect:/loginError";
//        }
        if (logo.equals("admin")) {
            return "redirect:/file";
        } else if (logo.equals("user")) {
            return "redirect:/us";
        } else {
            return "redirect:/loginError";
        }


    }

    @PostMapping("/changeView")
    public String moveRight(@RequestParam("direction") String direction) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();

        List<HashMap<String, String>> allData = shrek.getListOfData();
        NeededItems.clear();
        if (direction.equals("right")) {

            if (allData.size() > numerOfTableLines) {
                for (int i = startPosition; i < startPosition + numerOfTableLines; i++) {
                    NeededItems.add(allData.get(i));
                }
            } else {
                NeededItems = allData;
            }
            startPosition += numerOfTableLines;
        } else if (direction.equals("left")) {
            for (int i = startPosition - numerOfTableLines; i < startPosition; i++) {
                NeededItems.add(allData.get(i));
            }
            startPosition -= numerOfTableLines;
        }


        return "redirect:/file";

    }


}

