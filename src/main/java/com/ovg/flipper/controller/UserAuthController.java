package com.ovg.flipper.controller;

import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.dto.LocalLoginDto;
import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.service.AuthService;
import com.ovg.flipper.util.CookieManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class UserAuthController {

    private final AuthService authService;
    private final CookieManager cookieManager;

    public UserAuthController(AuthService authService, CookieManager cookieManager) {
        this.authService = authService;
        this.cookieManager = cookieManager;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpServletResponse response, @Valid LocalLoginDto user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        UserAuthDto userAuthDto = authService.login(user);
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
            return "redirect:/signup";
        }
        if(authService.registerUser(user, "ROLE_ADMIN")){
            return "redirect:/login";
        }
        return "redirect:/signup?error";
    }

    // TEST : admin page
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String showAdminPage() {
        return "admin";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }
}
