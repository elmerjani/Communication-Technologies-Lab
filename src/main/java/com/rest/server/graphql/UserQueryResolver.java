package com.rest.server.graphql;


import com.rest.server.models.User;
import com.rest.server.services.UserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class UserQueryResolver implements GraphQLQueryResolver {

    @Autowired
    private UserService userService;

    public Page<User> getAllUsers(int page, int size) {
        return userService.allUsers(PageRequest.of(page, size));
    }

}
