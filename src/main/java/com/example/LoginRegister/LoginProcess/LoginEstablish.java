package com.example.LoginRegister.LoginProcess;

import com.example.Constants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;

public class LoginEstablish {
    private static final Logger logger = LogManager.getLogger(LoginEstablish.class);
    public static String userNameTempLogin = "";

    public static String startLogin(String email, String password) {
        userNameTempLogin = "";
        return runConnectionLogin(email, password);
    }

    private static String runConnectionLogin(String email, String password) {
        String userRegistered = "";
        try {
            Connection loginConn = DriverManager.getConnection(Constants.url, Constants.user, Constants.password);
            Statement loginStat = loginConn.createStatement();
            ResultSet res = loginStat.executeQuery("SELECT verification_status, user_name FROM registered_users WHERE email = '" + email + "' AND password = crypt('" + password + "', password)");
            if (res.next()) {
                String verificationStatus = res.getString(1);
                userNameTempLogin = res.getString(2);
                if (verificationStatus.equals("not_verified")){
                    userRegistered = "not_verified";
                } else if (verificationStatus.equals("verified")){
                    userRegistered = "verified";
                }
            } else {
                userRegistered = "not_registered";
            }
            res.close();
            loginStat.close();
            loginConn.close();
        } catch (SQLException sqlException) {
            logger.error("Could not connect to database.", sqlException);
        }
        return userRegistered;
    }
}
