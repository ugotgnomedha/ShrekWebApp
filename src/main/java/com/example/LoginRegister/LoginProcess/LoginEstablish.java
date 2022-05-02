package com.example.LoginRegister.LoginProcess;

import com.example.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;

public class LoginEstablish {
    private static final Logger logger = LogManager.getLogger(LoginEstablish.class);

    public static Boolean startLogin(String email, String password) {
        return runConnectionLogin(email, password);
    }

    private static Boolean runConnectionLogin(String email, String password) {
        boolean flag = false;
        try {
            Connection loginConn = DriverManager.getConnection(Constants.url, Constants.user, Constants.password);
            Statement loginStat = loginConn.createStatement();
            // INSERT INTO registered_users (email, password, user_name) VALUES ('test@test.com',crypt('test123', gen_salt('bf')),'Darth');
            ResultSet res = loginStat.executeQuery("SELECT * FROM registered_users WHERE email = '" + email + "' AND password = crypt('" + password + "', password)");
            if (res.next()) {
                flag = true;
            } else {
                flag = false;
            }

            res.close();
            loginStat.close();
            loginConn.close();
        } catch (SQLException sqlException) {
            logger.error("Could not connect to database.", sqlException);
        }
        return flag;
    }
}
