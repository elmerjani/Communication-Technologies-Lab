package com.rest.server.graphql;

import com.rest.server.models.User;
import com.rest.server.models.UserDto;
import com.rest.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Optional;

@Controller
public class UserGraphQL {

    @Autowired
    private UserService userService;

    private Sinks.Many<User> userSink;
    private final Sinks.Many<String> userDeletedSink;

    public UserGraphQL() {
        this.userSink = Sinks.many().multicast().onBackpressureBuffer();
        this.userDeletedSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    private void resetUserSink() {
        System.out.println("Resetting userSink");
        this.userSink = Sinks.many().multicast().onBackpressureBuffer();
    }


    @SubscriptionMapping
    public Flux<String> userDeleted() {
        System.out.println("📡 Subscription `userDeleted` started...");

        return userDeletedSink.asFlux()
                .doOnNext(userId -> System.out.println("User deleted event sent: " + userId))
                .doOnError(error -> System.err.println("Subscription error: " + error.getMessage()))
                .doOnCancel(() -> System.out.println("Subscription cancelled"));
    }

    @SubscriptionMapping
    public Flux<User> userCreated() {
        return userSink.asFlux()
                .doOnNext(user -> {
                    System.out.println(" Sending event to client: " + user.getUserFirstName());
                })
                .doOnError(error -> System.err.println(" Subscription error: " + error.getMessage()))
                .doOnCancel(() -> {
                    System.out.println(" Subscription cancelled");
                    resetUserSink();
                })
                .doOnSubscribe(subscription -> {
                    System.out.println(" Subscription started...");
                });
    }


    @EventListener
    public void publishNewUser(User user) {
        System.out.println(" User received in UserGraphQL: " + user.getUserFirstName());
        userSink.tryEmitNext(user);
        System.out.println(" Emitting event for user: " + user.getUserFirstName());
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

        // Ajout du nouvel utilisateur à userSink
        //System.out.println(" Emitting event for user: " + createdUser.getUserFirstName());
        //userSink.tryEmitNext(createdUser);

        return createdUser;
    }

    @QueryMapping
    public Object getAllUsers(
            @Argument int page,
            @Argument int size,
            @Argument String sortBy,
            @Argument String sortOrder

    ) {
        // Définir l'ordre de tri (par défaut : tri par `userFirstName` en ASC)
        Sort.Direction direction = (sortOrder != null && sortOrder.equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        String sortingField = (sortBy != null) ? sortBy : "userFirstName";

        // Récupérer les utilisateurs avec pagination et tri
        Page<User> usersPage = userService.allUsers(PageRequest.of(page, size, Sort.by(direction, sortingField)));
        List<UserDto> users = usersPage.map(this::convertToDto).getContent();


        return users;
    }



    @QueryMapping
    public UserDto getUser(@Argument String id) {
        Optional<User> user = userService.singleUser(id);
        return user.map(this::convertToDto).orElse(null);
    }

    @QueryMapping
    public List<UserDto> searchUsers(@Argument String query, @Argument int page, @Argument int size) {
        Page<User> users = userService.searchUsers(query, PageRequest.of(page, size));
        return users.map(this::convertToDto).getContent();
    }

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

    @MutationMapping
    public boolean deleteUser(@Argument String id) {
        userService.deleteUser(id);
        return true;
    }

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
