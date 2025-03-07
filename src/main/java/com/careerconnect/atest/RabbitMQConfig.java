package com.careerconnect.atest;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${careerconnect.rabbitmq.exchange}")
    private String exchange;

    @Value("${careerconnect.rabbitmq.queue}")
    private String queue;

    @Value("${careerconnect.rabbitmq.routingkey}")
    private String routingKey;

    @Bean
    public Queue queue() {
        return new Queue(queue, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
////    SimpleMessageListenerContainer trong RabbitMQConfig và gán nó vào queue (job.alert.queue), container này sẽ "chiếm" queue đó và xử lý tất cả thông điệp bằng MessageListenerAdapter (trong trường hợp này là MyMessageListener).

//@RabbitListener trong JobAlertConsumer cũng cố gắng lắng nghe cùng queue (${careerconnect.rabbitmq.queue}). Tuy nhiên, Spring AMQP không cho phép hai listener cùng xử lý một queue trong cùng một context trừ khi được cấu hình đặc biệt.
//    @Bean
//    public SimpleMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(queue);
//        container.setMessageListener(listenerAdapter); // Sử dụng listenerAdapter để áp dụng converter
//        return container;
//    }

//    @Bean
//    public MessageListenerAdapter listenerAdapter(Jackson2JsonMessageConverter messageConverter) {
//        return new MessageListenerAdapter(new MyMessageListener(), messageConverter);
//    }
//
//    public static class MyMessageListener {
//        public void handleMessage(Object message) {
//            System.out.println("Received message: " + message);
//        }
//    }
}