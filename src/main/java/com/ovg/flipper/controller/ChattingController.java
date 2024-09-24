package com.ovg.flipper.controller;

import com.ovg.flipper.service.KafkaProducerService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChattingController {
    private final KafkaProducerService kafkaProducerService;

    public ChattingController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @MessageMapping("/message")
    public void sendMessage(String message) {
        // 메시지를 Kafka로 전송
        kafkaProducerService.sendMessage(message);
    }
}
