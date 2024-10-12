package com.ovg.flipper.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorViewController {

  @GetMapping("/403")
  public String accessDenied() {
    return "403";
  }
}