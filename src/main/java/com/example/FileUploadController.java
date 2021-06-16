package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import static com.example.Constants.*;

@Controller
public class FileUploadController {
    public static String uploadDirectory = System.getProperty("user.dir")+"/uploads";
//    Connection connection = DriverManager.getConnection(url, user, password);
//    ShrekBD shrek = new ShrekBD();

    private final StorageService storageService;
    List<Dictionary<String, String>> NeededItems = new ArrayList<>();

    @Autowired
    public FileUploadController(StorageService storageService) throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String home() throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        return "login";
    }

    @GetMapping("/file")
    public String listUploadedFiles(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        model.put("items", shrek.getListOfData());
        return "uploadForm";
    }

    @GetMapping("/sorting")
    public String sortin(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        model.put("items", NeededItems);
        return "sorting";
    }

    @GetMapping("/sorti")
    public String sorting(Map<String, Object> model) throws IOException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        model.put("items", NeededItems);
        return "sorting";
    }


    @PostMapping("/sorting")
    public String handleDomen(@RequestParam("domen") String domen,
                              RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        List<Dictionary<String, String>> items = new ArrayList<>();
        NeededItems.clear();
        items = shrek.getListOfData();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).get("email").contains(domen)) {
                NeededItems.add(items.get(i));
            }
        }
        return "redirect:/sorti";
    }

    @PostMapping("/find")
    public String handleKey(@RequestParam("key") String key,
                            RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();
        List<Dictionary<String, String>> items = new ArrayList<>();
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

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(url, user, password);
        ShrekBD shrek = new ShrekBD();

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
StringBuilder fileNames = new StringBuilder();
        Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
        fileNames.append(file.getOriginalFilename());
        Files.write(fileNameAndPath, file.getBytes());

        shrek.addFile(connection, file);

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

    @PostMapping("/log")
    public String handleLogo(@RequestParam("logo") String logo, @RequestParam("passwd") String passwd) throws SQLException, IOException, NoSuchAlgorithmException, NoSuchPaddingException {

        if (logo.equals("admin@admin") && passwd.equals("admin")) {
            return "redirect:/file";
        } else if (logo.equals("user@user") && passwd.equals("user")) {
            return "redirect:/us";
        } else {
            return "redirect:/loginError";
        }

    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}

