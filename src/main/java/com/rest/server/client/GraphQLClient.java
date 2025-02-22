package com.rest.server.client;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
public class GraphQLClient {

    private final WebClient webClient;

    public GraphQLClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080/graphql")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String getUserById(String userId) {
        String query = "{ getUser(id: \"" + userId + "\") { id firstName lastName email } }";

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("query", query))
                .retrieve()
                .bodyToMono(String.class)
                .block();  // ⚠️ Bloque l'exécution, utile en mode synchrone
    }

    public String createUser(String firstName, String lastName, String email) {
        String mutation = "mutation { createUser(firstName: \"" + firstName + "\", lastName: \"" + lastName + "\", email: \"" + email + "\") { id firstName lastName email } }";

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("query", mutation))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
