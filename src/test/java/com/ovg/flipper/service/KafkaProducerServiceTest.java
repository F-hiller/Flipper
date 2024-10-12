package com.ovg.flipper.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {

  @Mock
  private KafkaTemplate<String, String> kafkaTemplate;

  @InjectMocks
  private KafkaProducerService kafkaProducerService;

  @Test
  public void testSendMessage() {
    // Given
    String message = "Hello, Kafka!";

    // When
    kafkaProducerService.sendMessage(message);

    // Then
    verify(kafkaTemplate).send("chat-topic", message);
  }
}
