package com.ovg.flipper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontController {

    @GetMapping("/chat")
    public String chat() {
        return "chat"; // "chat.html" 파일을 반환
    }
}