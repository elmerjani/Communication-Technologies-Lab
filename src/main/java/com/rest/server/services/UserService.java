package com.rest.server.services;

import com.rest.server.exception.ResourceNotFoundException;
import com.rest.server.models.User;
import com.rest.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import reactor.core.publisher.Sinks;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final Sinks.Many<String> userDeletedSink = Sinks.many().multicast().onBackpressureBuffer();

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    public UserService(UserRepository userRepository,
                       ApplicationEventPublisher eventPublisher,
                       MessageSource messageSource,
                       LocaleResolver localeResolver) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    public User createUser(User user) {
        if (userRepository.findByUserEmail(user.getUserEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        validateUserFields(user);
        String encryptedPassword = new BCryptPasswordEncoder().encode(user.getUserPassword());
        user.setUserPassword(encryptedPassword);
        User savedUser = userRepository.save(user);

        eventPublisher.publishEvent(savedUser);
        return savedUser;
    }

    @Cacheable(value = "users", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<User> allUsers(Pageable pageable) {
        System.out.println("Fetching from database...");
        return userRepository.findAll(pageable);
    }

    public Optional<User> singleUser(String id) {
        return Optional.ofNullable(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getMessage("error.resourceNotFound"))));
    }

    public Page<User> searchUsers(String query, Pageable pageable) {
        return userRepository.findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCaseOrUserEmailContainingIgnoreCase(query, query, query, pageable);
    }

    private void validateUserFields(User user) {
        if (user.getUserFirstName() == null || user.getUserFirstName().isEmpty()) {
            throw new RuntimeException(getMessage("error.invalidParameters") + ": First name");
        }
        if (user.getUserLastName() == null || user.getUserLastName().isEmpty()) {
            throw new RuntimeException(getMessage("error.invalidParameters") + ": Last name");
        }
        if (user.getUserEmail() == null || user.getUserEmail().isEmpty()) {
            throw new RuntimeException(getMessage("error.invalidParameters") + ": Email");
        }
    }

    public User updateUser(String id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUserFirstName(updatedUser.getUserFirstName());
                    user.setUserLastName(updatedUser.getUserLastName());
                    user.setUserTitle(updatedUser.getUserTitle());
                    user.setUserPassword(updatedUser.getUserPassword());
                    user.setUserDateOfBirth(updatedUser.getUserDateOfBirth());
                    user.setUserPhone(updatedUser.getUserPhone());
                    user.setUserPicture(updatedUser.getUserPicture());
                    user.setUserLocationId(updatedUser.getUserLocationId());
                    return userRepository.save(user);
                }).orElseThrow(() -> new RuntimeException(getMessage("error.resourceNotFound")));
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
        System.out.println("Event Published (User Deleted): " + id);
        userDeletedSink.tryEmitNext(id);
    }

    private String getMessage(String code) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Locale locale = attr != null ? localeResolver.resolveLocale(attr.getRequest()) : Locale.ENGLISH;
        return messageSource.getMessage(code, null, locale);
    }
}
