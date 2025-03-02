package com.rest.server.configurations;

import com.rest.server.configurations.GraphQLWebSocketHandler;
import com.rest.server.graphql.UserGraphQL;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final UserGraphQL userGraphQL;

    public WebSocketConfig(UserGraphQL userGraphQL) {
        this.userGraphQL = userGraphQL;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GraphQLWebSocketHandler(userGraphQL), "/graphql-ws")
                .setAllowedOrigins("*");
    }
}





