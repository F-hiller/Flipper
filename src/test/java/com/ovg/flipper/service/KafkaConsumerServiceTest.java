package com.ovg.flipper.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @InjectMocks
  private KafkaConsumerService kafkaConsumerService;

  @Test
  public void testConsumeMessage() {
    // Given
    String message = "Hello, WebSocket!";

    // When
    kafkaConsumerService.consume(message);

    // Then
    verify(messagingTemplate).convertAndSend("/topic/messages", message);
  }
}
