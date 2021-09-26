package com.github.rivaldorodrigues.springstarter.infrastructure.security;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum Origin {

    WEB("jwt.token.expiration.web"),
    APP("jwt.token.expiration.app");

    private final String expirationTimeProperty;

    Origin(String expirationTimeProperty) {
        this.expirationTimeProperty = expirationTimeProperty;
    }

    public static Optional<Origin> getEnum(String origin) {
        return Arrays.stream(values())
                .filter(o -> o.name().equals(origin))
                .findFirst();
    }
}
