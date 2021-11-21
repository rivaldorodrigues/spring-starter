package com.github.rivaldorodrigues.springstarter.infrastructure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private MessageSource messageSource;

    @Autowired
    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String key) {
        return get(key, null);
    }

    public String get(String key, Object[] param) {
        return messageSource.getMessage(key, param, LocaleContextHolder.getLocale());
    }
}
