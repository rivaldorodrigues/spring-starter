package com.github.rivaldorodrigues.springstarter.infrastructure.security;

import com.github.rivaldorodrigues.springstarter.infrastructure.SystemClock;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtTokenProvider {

    private static final int MILLISECONDS_PER_MINUTE = 60000;
    private static final String AUTHORITIES_KEY = "auth";

    private String secretKey;
    private final Environment environment;
    private final SystemClock systemClock;

    private final Integer refreshInterval;
    private final Integer defaultExpirationInterval;

    @Autowired
    public JwtTokenProvider(Environment environment,
                            SystemClock systemClock,
                            @Value("${jwt.token.secret}") String secretKey,
                            @Value("${jwt.token.refresh:15}") Integer refreshInterval,
                            @Value("${jwt.token.expiration.default:60}") Integer defaultExpirationInterval) {

        this.secretKey = secretKey;
        this.environment = environment;
        this.systemClock = systemClock;
        this.refreshInterval = refreshInterval;
        this.defaultExpirationInterval = defaultExpirationInterval;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Authentication authentication, String origin) {

        int expirationTimeInMinutes = getExpirationTime(origin);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .setIssuedAt(systemClock.currentDate())
                .setExpiration(generateNewExpiration(expirationTimeInMinutes))
                .compact();
    }

    public String getUserLogin(String token) {

        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public TokenStatus validateToken(String token) {

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            Date tokenCreationDate = claimsJws.getBody().getIssuedAt();
            Date tokenExpirationDate = claimsJws.getBody().getExpiration();

            return tokenStateByTime(tokenCreationDate, tokenExpirationDate, refreshInterval);
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace:", e);
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token.");
            log.trace("Invalid JWT token trace:", e);
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token.");
            log.trace("Expired JWT token trace:", e);
            return TokenStatus.EXPIRED;
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace:", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace:", e);
        }

        return TokenStatus.INVALID;
    }

    public int getExpirationTime(String originHeader) {

        var origin = Origin.getEnum(originHeader);

        return origin.map(Origin::getExpirationTimeProperty)
                .map(environment::getProperty)
                .map(Integer::parseInt)
                .orElse(defaultExpirationInterval);
    }

    TokenStatus tokenStateByTime(Date tokenCreationTime, Date tokenExpirationTime, int refreshInterval) {

        if (tokenCreationTime == null || tokenExpirationTime == null) {
            return TokenStatus.INVALID;
        }

        var currentMilliseconds = systemClock.currentDate().getTime();
        var refreshInMilliseconds = getRefreshDate(tokenCreationTime, refreshInterval).getTime();
        var expirationInMilliseconds = tokenExpirationTime.getTime();

        if (currentMilliseconds > expirationInMilliseconds) {
            return TokenStatus.EXPIRED;
        } else if (currentMilliseconds > refreshInMilliseconds) {
            return TokenStatus.REFRESH;
        } else {
            return TokenStatus.VALID;
        }
    }

    private Date generateNewExpiration(int expirationTimeInMinutes) {
        var currentDate = systemClock.currentDate();
        return new Date(currentDate.getTime() + (expirationTimeInMinutes * MILLISECONDS_PER_MINUTE));
    }

    private Date getRefreshDate(Date tokenCreationDate, int refreshInterval) {
        return new Date(tokenCreationDate.getTime() + (refreshInterval * MILLISECONDS_PER_MINUTE));
    }
}
