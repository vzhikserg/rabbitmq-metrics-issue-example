package com.example.metrics.rabbitmq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import io.micrometer.core.annotation.Timed;

@Service
public class QueueService {
  private static final Logger log = LoggerFactory.getLogger(QueueService.class);

  @Timed
  @RabbitListener(queues = {"test-queue"}, containerFactory = "someContainerFactory")
  public void handleMessage(@Payload Message message) {
    log.info("Received one message");
  }
}
