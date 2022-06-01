package io.spring.examples.restapisource;

import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.integration.http.support.DefaultHttpHeaderMapper;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.integration.webflux.inbound.WebFluxInboundEndpoint;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;


@Configuration
//@ImportResource("classpath:META-INF/spring-integration/http-source.xml")
public class PostSimpleEvent {

    @Bean
    @ConditionalOnMissingBean
    public HeaderMapper<HttpHeaders> httpHeaderMapper() {
        DefaultHttpHeaderMapper defaultHttpHeaderMapper = DefaultHttpHeaderMapper.inboundMapper();
        defaultHttpHeaderMapper.setInboundHeaderNames(DefaultHttpHeaderMapper.HTTP_REQUEST_HEADER_NAME_PATTERN);
        return defaultHttpHeaderMapper;
    }

    @Bean
    public Publisher<Message<SimpleEvent>> httpSupplierFlow(
                                                   HeaderMapper<HttpHeaders> httpHeaderMapper,
                                                   ServerCodecConfigurer serverCodecConfigurer) {

        return IntegrationFlows.from(
                        WebFlux.inboundChannelAdapter("/event")
                                .requestPayloadType(SimpleEvent.class)
                                .statusCodeExpression(new ValueExpression<>(HttpStatus.ACCEPTED))
                                .headerMapper(httpHeaderMapper)
                                .codecConfigurer(serverCodecConfigurer)
                                .crossOrigin(crossOrigin ->
                                        crossOrigin.origin(CorsConfiguration.ALL)
                                                .allowedHeaders(CorsConfiguration.ALL)
                                                .allowCredentials(false))
                               .autoStartup(false))
        .enrichHeaders((headers) ->
                        headers.headerFunction(MessageHeaders.CONTENT_TYPE,
                                (message) ->
                                        (MediaType.APPLICATION_FORM_URLENCODED.equals(
                                                message.getHeaders().get(MessageHeaders.CONTENT_TYPE, MediaType.class)))
                                                ? MediaType.APPLICATION_JSON
                                                : null,
                                true))
                .toReactivePublisher();
    }

    @Bean
    public Supplier<Flux<Message<SimpleEvent>>> httpSupplier(
            Publisher<Message<SimpleEvent>> httpRequestPublisher,
            WebFluxInboundEndpoint webFluxInboundEndpoint) {

        return () -> Flux.from(httpRequestPublisher)
                .doOnSubscribe((subscription) -> webFluxInboundEndpoint.start())
                .doOnTerminate(webFluxInboundEndpoint::stop);
    }
}
