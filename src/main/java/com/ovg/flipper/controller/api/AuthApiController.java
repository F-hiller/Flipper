package com.ovg.flipper.controller.api;

import com.ovg.flipper.dto.LocalLoginDto;
import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.dto.UserSignupDto;
import com.ovg.flipper.service.AuthService;
import com.ovg.flipper.util.CookieManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api")
public class AuthApiController {

  private final AuthService authService;
  private final CookieManager cookieManager;

  @Autowired
  public AuthApiController(AuthService authService, CookieManager cookieManager) {
    this.authService = authService;
    this.cookieManager = cookieManager;
  }

  @PostMapping("/login")
  public String login(HttpServletResponse response, @Valid LocalLoginDto user,
      BindingResult bindingResult) {
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

  @PostMapping("/signup")
  public String signUp(@Valid UserSignupDto user, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "redirect:/signup";
    }
    if (authService.registerUser(user, "ROLE_ADMIN")) {
      return "redirect:/login";
    }

    return "redirect:/signup?error";
  }
}