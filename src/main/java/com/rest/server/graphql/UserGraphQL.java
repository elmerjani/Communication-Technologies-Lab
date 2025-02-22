package com.rest.server.graphql;

import com.rest.server.models.User;
import com.rest.server.models.UserDto;
import com.rest.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Optional;

@Component
public class UserGraphQL {

    @Autowired
    private UserService userService;

    // Equivalent de getAllUsersV1 avec pagination
    @QueryMapping
    public List<UserDto> getAllUsers(@Argument int page, @Argument int size) {
        Page<User> usersPage = userService.allUsers(PageRequest.of(page, size));
        return usersPage.map(this::convertToDto).getContent();
    }

    // Equivalent de getSingleUser
    @QueryMapping
    public UserDto getUser(@Argument String id) {
        Optional<User> user = userService.singleUser(id);
        return user.map(this::convertToDto).orElse(null);
    }

    // Equivalent de searchUsers
    @QueryMapping
    public List<UserDto> searchUsers(@Argument String query, @Argument int page, @Argument int size) {
        Page<User> users = userService.searchUsers(query, PageRequest.of(page, size));
        return users.map(this::convertToDto).getContent();
    }

    // Equivalent de createUser
    @MutationMapping
    public User createUser(@Argument String firstName, @Argument String lastName, @Argument String email) {
        User user = new User();
        user.setUserFirstName(firstName);
        user.setUserLastName(lastName);
        user.setUserEmail(email);
        return userService.createUser(user);
    }

    // Equivalent de updateUser
    @MutationMapping
    public User updateUser(@Argument String id, @Argument String firstName, @Argument String lastName) {
        User user = userService.singleUser(id).orElseThrow();
        if (firstName != null) user.setUserFirstName(firstName);
        if (lastName != null) user.setUserLastName(lastName);
        return userService.updateUser(id, user);
    }

    // Equivalent de deleteUser
    @MutationMapping
    public boolean deleteUser(@Argument String id) {
        userService.deleteUser(id);
        return true;
    }

    // Conversion User -> UserDto
    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setUserTitle(user.getUserTitle());
        userDto.setUserFirstName(user.getUserFirstName());
        userDto.setUserLastName(user.getUserLastName());
        userDto.setUserGender(user.getUserGender());
        userDto.setUserEmail(user.getUserEmail());
        userDto.setUserDateOfBirth(user.getUserDateOfBirth());
        userDto.setUserRegisterDate(user.getUserRegisterDate());
        userDto.setUserPhone(user.getUserPhone());
        userDto.setUserPicture(user.getUserPicture());
        userDto.setUserLocationId(user.getUserLocationId());
        return userDto;
    }
}
