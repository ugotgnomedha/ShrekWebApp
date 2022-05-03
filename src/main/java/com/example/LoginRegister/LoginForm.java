package com.example.LoginRegister;

public class LoginForm {
    private String emailLogin;
    private String passwordLogin;

    public static String emailTempLogin = "";
    public static String passwordTempLogin = "";

    public String getEmailLogin() {
        return emailLogin;
    }

    public void setEmailLogin(String emailLogin) {
        this.emailLogin = emailLogin;
        emailTempLogin = emailLogin;
    }

    public String getPasswordLogin() {
        return passwordLogin;
    }

    public void setPasswordLogin(String passwordLogin) {
        this.passwordLogin = passwordLogin;
        passwordTempLogin = passwordLogin;
    }
}
