package com.github.rivaldorodrigues.springstarter.infrastructure.service;

import com.github.rivaldorodrigues.springstarter.domain.aggregate.user.User;
import com.github.rivaldorodrigues.springstarter.domain.aggregate.user.UserDetails;
import com.github.rivaldorodrigues.springstarter.domain.aggregate.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final MessageService message;
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsService(UserRepository userRepository, MessageService message) {
        this.message = message;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) {

        var userByLogin = userRepository.findByLogin(login);

        return userByLogin
                .map(UserDetails::fromUser)
                .orElseThrow(() -> new UsernameNotFoundException(message.get("error.user.not-found")));

    }

}
