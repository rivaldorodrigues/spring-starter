package com.github.rivaldorodrigues.springstarter.infrastructure.security;

import com.github.rivaldorodrigues.springstarter.infrastructure.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Service
public class Http401AuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private MessageService message;

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException {

        log.error("Unauthorized user. Erro - {}", e.getMessage());
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, message.get("error.user.unauthorized"));
    }
}
