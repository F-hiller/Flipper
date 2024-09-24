package com.ovg.flipper.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;

    public KafkaConsumerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "chat-topic", groupId = "chat-group")
    public void consume(String message) {
        // WebSocket을 통해 구독한 클라이언트들에게 메시지를 전송
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}
