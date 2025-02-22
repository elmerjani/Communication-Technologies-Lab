package com.rest.server.graphql;


import com.rest.server.client.GraphQLClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/graphql")
public class GraphQLTestController {

    private final GraphQLClient graphQLClient;

    public GraphQLTestController(GraphQLClient graphQLClient) {
        this.graphQLClient = graphQLClient;
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable String id) {
        return graphQLClient.getUserById(id);
    }

    @PostMapping("/user")
    public String createUser(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {
        return graphQLClient.createUser(firstName, lastName, email);
    }
}
