package com.example.LoginRegister.VerificationEmailProcess;

import com.example.Constants;
import com.example.LoginRegister.RegisterForm;

import java.sql.*;

public class VerificationEmailEstablish {
    public static Boolean startVerification(String verificationCode) {
        return runVerification(verificationCode);
    }

    private static Boolean runVerification(String verificationCode) {
        Boolean confirmedCode = false;
        try {
            Connection connection = DriverManager.getConnection(Constants.url, Constants.user, Constants.password);
            Statement statement = connection.createStatement();

            ResultSet res = statement.executeQuery("SELECT verification_code FROM registered_users WHERE email = '"+RegisterForm.emailTempRegister+"'");
            String verifCode = "";
            while (res.next()){
                verifCode = res.getString(1);
            }
            res.close();

            if (verificationCode.equals(verifCode)) {
                confirmedCode = true;
                statement.executeUpdate("INSERT INTO registered_users(email, password, user_name, verification_status, verification_code)" +
                        " VALUES('"+ RegisterForm.emailTempRegister+"', '"+RegisterForm.passwordTempRegister+"', '"+RegisterForm.userNameTempRegister+"', 'verified', '"+verificationCode+"') ON CONFLICT (email) DO UPDATE SET verification_status = 'verified'");
            }

            statement.close();
            connection.close();
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return confirmedCode;
    }
}
