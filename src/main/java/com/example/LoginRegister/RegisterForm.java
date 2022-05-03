package com.example.LoginRegister;

public class RegisterForm {

    private String emailRegister;
    private String passwordRegister;
    private String userNameRegister;

    public static String emailTempRegister = "";
    public static String passwordTempRegister = "";
    public static String userNameTempRegister = "";

    public String getEmailRegister() {
        return emailRegister;
    }

    public void setEmailRegister(String emailRegister) {
        this.emailRegister = emailRegister;
        emailTempRegister = emailRegister;
    }

    public String getPasswordRegister() {
        return passwordRegister;
    }

    public void setPasswordRegister(String passwordRegister) {
        this.passwordRegister = passwordRegister;
        passwordTempRegister = passwordRegister;
    }

    public String getUserNameRegister() {
        return userNameRegister;
    }

    public void setUserNameRegister(String userNameRegister) {
        this.userNameRegister = userNameRegister;
        userNameTempRegister = userNameRegister;
    }
}
