package com.ovg.flipper.controller.api;

import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.dto.UserLoginDto;
import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.service.UserAuthService;
import com.ovg.flipper.util.CookieManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthApiController {
    private final UserAuthService userAuthService;
    private final CookieManager cookieManager;

    @Autowired
    public AuthApiController(UserAuthService userAuthService, CookieManager cookieManager) {
        this.userAuthService = userAuthService;
        this.cookieManager = cookieManager;
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
}
