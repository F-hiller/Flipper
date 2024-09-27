package com.ovg.flipper.controller;

import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.service.UserAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserAuthController {

    private final UserAuthService signUpService;

    public UserAuthController(UserAuthService signUpService) {
        this.signUpService = signUpService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new UserSignupDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(UserSignupDto user) {
        // 회원가입 로직을 서비스에 위임
        signUpService.registerUser(user);
        return "redirect:/login";
    }
}
