package com.ovg.flipper.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatViewController {

    @GetMapping("/chat")
    public String chat() {
        return "chat";  // "chat.html" 반환
    }
}
