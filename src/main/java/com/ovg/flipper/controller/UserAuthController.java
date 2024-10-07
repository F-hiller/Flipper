package com.ovg.flipper.controller;

import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.dto.UserLoginDto;
import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.service.UserAuthService;
import com.ovg.flipper.util.CookieManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class UserAuthController {

    private final UserAuthService userAuthService;
    private final CookieManager cookieManager;

    public UserAuthController(UserAuthService userAuthService, CookieManager cookieManager) {
        this.userAuthService = userAuthService;
        this.cookieManager = cookieManager;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletResponse response, @Valid UserLoginDto user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        UserAuthDto userAuthDto = userAuthService.login(user);
        if (userAuthDto == null) {
            return "redirect:/login?error";
        }

        cookieManager.addCookie("ACCESS_TOKEN", userAuthDto.getAccessToken(), response);
        cookieManager.addCookie("REFRESH_TOKEN", userAuthDto.getRefreshToken(), response);

        return "redirect:/mypage";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new UserSignupDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(@Valid UserSignupDto user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        if(userAuthService.registerUser(user)){
            return "redirect:/login";
        }
        return "redirect:/signup?error";
    }

    // TEST : admin page
    @GetMapping("/admin")
    public String showAdminPage() {
        return "admin";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }
}
