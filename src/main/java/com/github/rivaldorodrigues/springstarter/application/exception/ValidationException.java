package com.github.rivaldorodrigues.springstarter.application.exception;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

@Getter
public class ValidationException extends NestedRuntimeException {

    @Nullable
    private final String message;

    private Map<String, List<String>> validation;

    private ValidationException(@Nullable String message, Map<String, List<String>> validation) {
        super(message);
        this.message = message;
        this.validation = validation;
    }

    public static class Builder {

        @Nullable
        private String message;

        private Map<String, List<String>> validation = Maps.newHashMap();

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder addValidationError(String field, String message) {
            validation.putIfAbsent(field, Lists.newArrayList());
            validation.get(field).add(message);

            return this;
        }

        public ValidationException build() {
            return new ValidationException(message, validation);
        }
    }

    public boolean hasError() {
        return !this.validation.isEmpty();
    }
}
