package ru.system.monitoring.config.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;

@Configuration
//@EnableWebSocketMessageBroker
public class SocketConfig implements WebSocketMessageBrokerConfigurer {

//    @Override // webSocket config
//    public void configureMessageBroker(MessageBrokerRegistry registry) {
//        registry.enableSimpleBroker("/topic");
//        registry.setApplicationDestinationPrefixes("/app");
////        WebSocketMessageBrokerConfigurer.super.configureMessageBroker(registry);
//    }

//    @Override // webSocket config
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
////        registry.addEndpoint("/socket");
//        registry.addEndpoint("/socket").setAllowedOrigins("http://localhost:3000").withSockJS(); // allowedOrigins for react front service
////        WebSocketMessageBrokerConfigurer.super.registerStompEndpoints(registry);
//    }

    @Bean
    public HandlerMapping webSocketHandlerMapping(WebSocketHandler webSocketHandler) {
        Map<String, WebSocketHandler> map = Map.of("/socket", webSocketHandler);
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        mapping.setUrlMap(map);
        return mapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
