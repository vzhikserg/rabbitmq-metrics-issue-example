package com.example.metrics.rabbitmq.config;

import com.rabbitmq.client.Channel;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
  private CachingConnectionFactory connectionFactory;

  @Inject
  public RabbitMQConfig(CachingConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Bean
  public FanoutExchange exchange() {
    return new FanoutExchange("test-exchange");
  }

  @Bean
  public Queue queue() throws IOException {
    // FIXME everything is fine if this method is not used
    consumerCount("test-queue");
    return new Queue("test-queue");
  }

  @Bean
  public Binding binding() throws IOException {
    return BindingBuilder.bind(queue()).to(exchange());
  }

  @Bean(name = "someContainerFactory")
  public SimpleRabbitListenerContainerFactory someContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, this.connectionFactory);
    return factory;
  }

  @Bean(name = "anotherContainerFactory")
  public SimpleRabbitListenerContainerFactory anotherContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, this.connectionFactory);
    return factory;
  }

  private long consumerCount(String queueName) throws IOException {
    // FIXME this is the issue! Another connection is created!
    Channel channel = this.connectionFactory.createConnection().createChannel(false);
    try {
      return channel.consumerCount(queueName);
    } finally {
      try {
        channel.close();
      } catch (TimeoutException ignored) {
      }
    }
  }
}
