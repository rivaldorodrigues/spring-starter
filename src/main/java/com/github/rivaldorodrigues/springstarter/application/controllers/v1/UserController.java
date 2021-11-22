package com.github.rivaldorodrigues.springstarter.application.controllers.v1;

import com.github.rivaldorodrigues.springstarter.application.exception.ValidationException;
import com.github.rivaldorodrigues.springstarter.domain.aggregate.user.User;
import com.github.rivaldorodrigues.springstarter.domain.aggregate.user.UserRepository;
import com.github.rivaldorodrigues.springstarter.infrastructure.service.MessageService;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "v1/users")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private MessageService message;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @GetMapping
    public List<User> listAll() {
        return userRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public User find(@PathVariable("id") Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //@PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public User save(@RequestBody @Valid User newUser) {

        validateUser(newUser);

        String encryptedPassword = passwordEncoder.encode(newUser.getPassword());

        var usuario = User.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .login(newUser.getLogin().toLowerCase())
                .password(encryptedPassword)
                .profiles(newUser.getProfiles())
                .build();

        return userRepository.save(usuario);
    }

    @PatchMapping(value = "/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public User update(@PathVariable("id") Long id, @RequestBody User user) {

        Preconditions.checkNotNull(user);
        User dbUser = findUserById(id);

        user.setId(id);
        validateUser(user);

        if (StringUtils.isNotBlank(user.getPassword())) {
            String encryptedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encryptedPassword);
        } else {
            user.setPassword(dbUser.getPassword());
        }

        user.setLogin(user.getLogin().toLowerCase());
        return userRepository.save(user);
    }

    @DeleteMapping(value = "/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public void delete(@PathVariable("id") Long id) {
        findUserById(id);
        userRepository.deleteById(id);
    }

    private User findUserById(Long id) {

        var dbUser = userRepository.findById(id);

        if (dbUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message.get("error.user.not-found"));
        }

        return dbUser.get();
    }

    private void validateUser(User user) {

        var builder = new ValidationException.Builder();

        var dbUser = userRepository.findByLogin(user.getLogin());

        if (dbUser.isPresent() && !dbUser.get().getId().equals(user.getId())) {
            builder.addValidationError("login", message.get("error.login.in-use"));
        }

        dbUser = userRepository.findByEmail(user.getEmail());

        if (dbUser.isPresent() && !dbUser.get().getId().equals(user.getId())) {
            builder.addValidationError("email", message.get("error.email.in-use"));
        }

        if (builder.hasError()) {
            throw builder.build();
        }
    }

}
