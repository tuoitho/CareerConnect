package com.careerconnect.atest;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableRabbit   Spring Boot da bat mac dinh roi ?
public class RabbitMQConfig {

    @Value("${careerconnect.rabbitmq.topic.exchange}")
    private String topicExchange;

    @Value("${careerconnect.rabbitmq.direct.exchange}")
    private String directExchange;

    @Value("${careerconnect.rabbitmq.queue}")
    private String queue;
    @Value("${careerconnect.rabbitmq.application.queue}")
    private String jobApplicationQueue;
    @Value("${careerconnect.rabbitmq.routingkey}")
    private String routingKey;

    @Value("${careerconnect.rabbitmq.application.routingkey}")
    private String jobApplicationRoutingKey;

    @Bean
    public Queue queue() {
        return new Queue(queue, true);
    }
    @Bean
    public Queue jobApplicationQueue() {
        return new Queue(jobApplicationQueue, true);
    }


    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(topicExchange);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(directExchange);
    }
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public Binding jobApplicationBinding(Queue jobApplicationQueue, DirectExchange exchange) {
        return BindingBuilder.bind(jobApplicationQueue).to(exchange).with(jobApplicationRoutingKey);
    }
//    Bạn có thể bỏ đoạn này nếu:
//             Bạn chỉ làm việc với chuỗi đơn giản (String) thay vì đối tượng JSON.
//            Bạn đang gửi/nhận message bằng byte array (byte[]).
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

}