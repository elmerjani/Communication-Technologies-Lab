package com.rest.server.configurations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.server.graphql.UserGraphQL;
import com.rest.server.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import reactor.core.publisher.Flux;

@Component
public class GraphQLWebSocketHandler extends AbstractWebSocketHandler {
    private final UserGraphQL userGraphQL;

    @Autowired
    public GraphQLWebSocketHandler(UserGraphQL userGraphQL) {
        this.userGraphQL = userGraphQL;
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode payload = mapper.readTree(message.getPayload());

        if (payload.get("type").asText().equals("connection_init")) {
            session.sendMessage(new TextMessage("{\"type\":\"connection_ack\"}"));
        } else if (payload.get("type").asText().equals("start")) {
            Flux<User> userFlux = userGraphQL.userCreated();

            userFlux.subscribe(user -> {
                try {
                    String userJson = mapper.writeValueAsString(user);
                    session.sendMessage(new TextMessage("{\"type\":\"data\",\"id\":\"" + payload.get("id").asText() + "\",\"payload\":{\"data\":{\"userCreated\":" + userJson + "}}}"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

