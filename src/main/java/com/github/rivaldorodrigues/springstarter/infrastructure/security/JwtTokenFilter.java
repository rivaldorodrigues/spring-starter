package com.github.rivaldorodrigues.springstarter.infrastructure.security;

import com.github.rivaldorodrigues.springstarter.infrastructure.service.UserDetailsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";
    private static final String ORIGIN_HEADER = "X-Origin";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var currentToken = getTokenFromHeader(request);

        if (StringUtils.hasText(currentToken)) {

            var tokenStatus = jwtTokenProvider.validateToken(currentToken);

            switch (tokenStatus) {
                case VALID -> {
                    setAuthentication(currentToken, request);
                    response.setHeader(AUTHORIZATION_HEADER, BEARER + currentToken);
                }
                case REFRESH -> {
                    Authentication auth = setAuthentication(currentToken, request);
                    String newToken = jwtTokenProvider.createToken(auth, getOriginFromHeader(request));
                    response.setHeader(AUTHORIZATION_HEADER, BEARER + newToken);
                }
                case EXPIRED, INVALID -> SecurityContextHolder.getContext().setAuthentication(null);
            }
        }

        filterChain.doFilter(request, response);
    }

    private Authentication setAuthentication(String token, HttpServletRequest request) {

        var userLogin = jwtTokenProvider.getUserLogin(token);
        var detalhesUsuario = userDetailsService.loadUserByUsername(userLogin);

        var authentication = new UsernamePasswordAuthenticationToken(detalhesUsuario, null, detalhesUsuario.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    private String getTokenFromHeader(HttpServletRequest request) {

        var bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.replace(BEARER, "");
        }

        return null;
    }

    private String getOriginFromHeader(HttpServletRequest request) {
        return request.getHeader(ORIGIN_HEADER);
    }
}
