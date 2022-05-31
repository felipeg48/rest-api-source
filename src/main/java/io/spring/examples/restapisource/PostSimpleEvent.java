package io.spring.examples.restapisource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.http.dsl.Http;


@Configuration
//@ImportResource("classpath:META-INF/spring-integration/http-source.xml")
public class PostSimpleEvent {


    @Bean
    public IntegrationFlow inbound() {
        return IntegrationFlows.from(Http.inboundGateway("/event")
                        .requestMapping(m -> m.methods(HttpMethod.POST))
                        .requestPayloadType(SimpleEvent.class)
                )
                .logAndReply();
    }



}
