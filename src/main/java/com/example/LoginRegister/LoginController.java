package com.example.LoginRegister;

import com.example.LoginRegister.LoginProcess.LoginEstablish;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
    @GetMapping("/loginPage")
    public String loginForm(Model loginModel) {
        loginModel.addAttribute("loginForm", new LoginForm());
        return "loginPage";
    }

    @PostMapping("/loginPage")
    public String loginSubmit(@ModelAttribute("loginForm") LoginForm loginForm) {
        if (LoginEstablish.startLogin(loginForm.getEmailLogin(), loginForm.getPasswordLogin())) {
            return "redirect:/file";
        } else {
            return "loginPage";
        }
    }
}
