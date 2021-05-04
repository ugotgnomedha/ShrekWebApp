package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static com.example.Constants.*;

@Controller
public class GreetingController {
    Connection connection = DriverManager.getConnection(url, user, password);
    ShrekBD shrek = new ShrekBD();

    public GreetingController() throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
    }

    @GetMapping("/")
    public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name, Map<String, Object> model) throws SQLException, NoSuchAlgorithmException, NoSuchPaddingException {
        model.put("name", name);

//        shrek.addData(connection);
        return "login";
    }


}