package com.example.LoginRegister;

import com.example.LoginRegister.RegisterProcess.EmailAuth;
import com.example.LoginRegister.RegisterProcess.RegisterEstablish;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {
    @GetMapping("/registerPage")
    public String registerForm(Model registerModel) {
        registerModel.addAttribute("registerForm", new RegisterForm());
        return "registerPage";
    }

    @PostMapping("/registerPage")
    public String registerSubmit(@ModelAttribute("registerForm") RegisterForm registerForm) {
        System.out.println(registerForm.getEmailRegister());
        System.out.println(registerForm.getPasswordRegister());
        System.out.println(registerForm.getUserNameRegister());
        if (RegisterEstablish.startLogin(registerForm.getEmailRegister(), registerForm.getPasswordRegister(), registerForm.getUserNameRegister())){
            EmailAuth.sendAuthEmail(registerForm.getEmailRegister(), registerForm.getUserNameRegister());
            return "redirect:/loginPage";
        } else {
            return "registerPage";
        }
    }
}
