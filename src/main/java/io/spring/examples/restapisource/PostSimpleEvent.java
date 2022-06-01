package io.spring.examples.restapisource;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.http.dsl.Http;


@Configuration
//@ImportResource("classpath:META-INF/spring-integration/http-source.xml")
public class PostSimpleEvent {


    @Bean
    public IntegrationFlow inbound(RabbitTemplate rabbitTemplate) {
        return IntegrationFlows.from(Http.inboundGateway("/event")
                        .requestMapping(m -> m.methods(HttpMethod.POST))
                        .requestPayloadType(SimpleEvent.class)
                        .replyChannel(MessageChannels.queue().get())
                )
                .<SimpleEvent>handle((p,h) -> {
                    rabbitTemplate.convertAndSend(p);
                    return p;
                })
                .logAndReply();



    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange("simple");
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
       return rabbitTemplate;
    }
}
