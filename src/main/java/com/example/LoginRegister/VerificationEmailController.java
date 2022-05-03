package com.example.LoginRegister;

import com.example.LoginRegister.RegisterProcess.EmailAuth;
import com.example.LoginRegister.VerificationEmailProcess.VerificationEmailEstablish;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class VerificationEmailController {
    @GetMapping("/verificationEmailPage")
    public String verificationEmailForm(Model loginModel) {
        loginModel.addAttribute("verificationEmailForm", new VerificationEmailForm());
        return "verificationEmailPage";
    }

    @PostMapping("/verificationEmailPage")
    public String verificationEmailSubmit(@ModelAttribute("verificationEmailForm") VerificationEmailForm verificationEmailForm) {
        if (VerificationEmailEstablish.startVerification(verificationEmailForm.getVerificationCode())){
            return "redirect:/loginPage";
        } else {
            return "verificationEmailPage";
        }
    }
}
