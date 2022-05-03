package com.example.LoginRegister.RegisterProcess;

import com.example.Constants;
import com.example.LoginRegister.RegisteredUsersTableCreate.CreateUsersTable;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;

public class RegisterEstablish {
    private static final Logger logger = LogManager.getLogger(RegisterEstablish.class);

    public static Boolean startLogin(String email, String password, String userName) {
        return runConnectionRegister(email, password, userName);
    }

    private static Boolean runConnectionRegister(String email, String password, String userName) {
        boolean flag = false;
        try {
            CreateUsersTable.createUsersTable(); // Check if registered_users table exists.

            Connection registerConn = DriverManager.getConnection(Constants.url, Constants.user, Constants.password);
            Statement registerStat = registerConn.createStatement();
            ResultSet res = registerStat.executeQuery("SELECT * FROM registered_users WHERE email = '" + email + "'");
            if (res.next()) {
                logger.info("email: " + email + " is already registered.");
                flag = false;
            } else {
                registerStat.executeUpdate("INSERT INTO registered_users (email, password, user_name, verification_status, verification_code) VALUES ('" + email + "',crypt('" + password + "', gen_salt('bf')),'" + userName + "', 'not_verified', '0')");
                flag = true;
            }

            res.close();
            registerStat.close();
            registerConn.close();
        } catch (SQLException sqlException) {
            logger.error("Could not connect to database.", sqlException);
        }
        return flag;
    }
}
