package com.example.LoginRegister;

import com.example.LoginRegister.LoginProcess.LoginEstablish;
import com.example.LoginRegister.RegisterProcess.EmailAuth;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static javax.swing.JOptionPane.showMessageDialog;

@Controller
public class LoginController {
    @GetMapping("/loginPage")
    public String loginForm(Model loginModel) {
        loginModel.addAttribute("loginForm", new LoginForm());
        return "loginPage";
    }

    @PostMapping("/loginPage")
    public String loginSubmit(@ModelAttribute("loginForm") LoginForm loginForm) {
        String answer = LoginEstablish.startLogin(loginForm.getEmailLogin(), loginForm.getPasswordLogin());
        if (answer.equals("verified")) {
            return "redirect:/file";
        } else if (answer.equals("not_verified")){
            EmailAuth.sendAuthEmail(loginForm.getEmailLogin(), LoginEstablish.userNameTempLogin, loginForm.getPasswordLogin());
            return "redirect:/verificationEmailPage";
        } else {
            return "redirect:/registerPage";
        }
    }
}
