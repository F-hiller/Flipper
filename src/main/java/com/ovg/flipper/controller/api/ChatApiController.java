package com.ovg.flipper.controller.api;

import com.ovg.flipper.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class ChatApiController {

  private final KafkaProducerService kafkaProducerService;

  @Autowired
  public ChatApiController(KafkaProducerService kafkaProducerService) {
    this.kafkaProducerService = kafkaProducerService;
  }

  @MessageMapping("/message")
  public void sendMessage(String message) {
    kafkaProducerService.sendMessage(message);
  }
}