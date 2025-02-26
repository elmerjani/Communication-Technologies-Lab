package com.rest.server.graphql;

import com.rest.server.models.User;
import com.rest.server.models.UserDto;
import com.rest.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Controller
public class UserGraphQL {

    @Autowired
    private UserService userService;
/**/
        // Utilisez un Sinks pour émettre les événements
        private final Sinks.Many<User> userCreatedSink = Sinks.many().replay().all();

        // Méthode de souscription pour l'utilisateur créé
        @SubscriptionMapping
        public Mono<User> userCreated() {
            return userCreatedSink.asFlux().next();
        }

        @MutationMapping
        public User createUser(@Argument String userTitle, @Argument String userFirstName, @Argument String userLastName,
                               @Argument String userGender, @Argument String userEmail, @Argument String userPassword,
                               @Argument String userDateOfBirth, @Argument String userRegisterDate, @Argument String userPhone,
                               @Argument String userPicture, @Argument String userLocationId) {
            User user = new User();
            user.setUserTitle(userTitle);
            user.setUserFirstName(userFirstName);
            user.setUserLastName(userLastName);
            user.setUserGender(userGender);
            user.setUserEmail(userEmail);
            user.setUserPassword(userPassword);
            user.setUserDateOfBirth(userDateOfBirth);
            user.setUserRegisterDate(userRegisterDate);
            user.setUserPhone(userPhone);
            user.setUserPicture(userPicture);
            user.setUserLocationId(userLocationId);

            User createdUser = userService.createUser(user);

            // Après avoir créé un utilisateur, émettez l'événement de souscription
            userCreatedSink.tryEmitNext(createdUser);

            return createdUser;
        }

/**/
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
/*
    @MutationMapping
    public User createUser(@Argument String userTitle,@Argument String userFirstName,@Argument String userLastName,@Argument String userGender,@Argument String userEmail,
                           @Argument String userPassword,@Argument String userDateOfBirth,@Argument String userRegisterDate,@Argument String userPhone,@Argument String userPicture,@Argument String userLocationId) {
        User user = new User();
        user.setUserTitle(userTitle);
        user.setUserFirstName(userFirstName);
        user.setUserLastName(userLastName);
        user.setUserGender(userGender);
        user.setUserEmail(userEmail);
        user.setUserPassword(userPassword);
        user.setUserDateOfBirth(userDateOfBirth);
        user.setUserRegisterDate(userRegisterDate);
        user.setUserPhone(userPhone);
        user.setUserPicture(userPicture);
        user.setUserLocationId(userLocationId);

        return userService.createUser(user);
    }*/
    // Equivalent de updateUser
    @MutationMapping
    public User updateUser(
            @Argument String id,
            @Argument String userTitle,
            @Argument String userFirstName,
            @Argument String userLastName,
            @Argument String userGender,
            @Argument String userEmail,
            @Argument String userDateOfBirth,
            @Argument String userRegisterDate,
            @Argument String userPhone,
            @Argument String userPicture,
            @Argument String userLocationId
    ) {
        User existingUser = userService.singleUser(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (userTitle != null) existingUser.setUserTitle(userTitle);
        if (userFirstName != null) existingUser.setUserFirstName(userFirstName);
        if (userLastName != null) existingUser.setUserLastName(userLastName);
        if (userGender != null) existingUser.setUserGender(userGender);
        if (userEmail != null) existingUser.setUserEmail(userEmail);
        if (userDateOfBirth != null) existingUser.setUserDateOfBirth(userDateOfBirth);
        if (userRegisterDate != null) existingUser.setUserRegisterDate(userRegisterDate);
        if (userPhone != null) existingUser.setUserPhone(userPhone);
        if (userPicture != null) existingUser.setUserPicture(userPicture);
        if (userLocationId != null) existingUser.setUserLocationId(userLocationId);

        return userService.updateUser(id, existingUser);
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
