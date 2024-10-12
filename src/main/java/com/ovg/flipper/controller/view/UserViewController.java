package com.ovg.flipper.controller.view;

import com.ovg.flipper.dto.UserSignupDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserViewController {

  @GetMapping("/login")
  public String showLoginPage() {
    return "login";
  }

  @GetMapping("/signup")
  public String showSignUpForm(Model model) {
    model.addAttribute("user", new UserSignupDto());
    return "signup";
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/admin")
  public String showAdminPage() {
    return "admin";
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  @GetMapping("/mypage")
  public String myPage(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Object principal = authentication.getPrincipal();
    UserDetails userDetails = (UserDetails) principal;
    String username = userDetails.getUsername();

    model.addAttribute("username", username);

    return "mypage";
  }
}