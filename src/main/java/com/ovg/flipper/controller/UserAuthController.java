package com.ovg.flipper.controller;

import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.dto.UserLoginDto;
import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.service.UserAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletResponse response, UserLoginDto user) {
        UserAuthDto userAuthDto = userAuthService.login(user);
        if (userAuthDto == null) {
            return "redirect:/login?error";
        }

        Cookie accessToken = new Cookie("ACCESS_TOKEN", userAuthDto.getAccessToken());
        Cookie refreshToken = new Cookie("REFRESH_TOKEN", userAuthDto.getRefreshToken());

        accessToken.setHttpOnly(true);
        accessToken.setAttribute("SameSite", "Strict");
        refreshToken.setHttpOnly(true);
        refreshToken.setAttribute("SameSite", "Strict");

        // TODO : MUST DELETE! BEFORE COMMIT
        log.warn("access : {}", accessToken);
        log.warn("refresh : {}", refreshToken);

        response.addCookie(accessToken);
        response.addCookie(refreshToken);
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new UserSignupDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signUp(UserSignupDto user) {
        // 회원가입 로직을 서비스에 위임
        userAuthService.registerUser(user);
        return "redirect:/login";
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
