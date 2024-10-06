package com.ovg.flipper.util;

import com.ovg.flipper.dto.UserAuthDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;
    private static final int ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60; // 1hour
    private static final int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 1week
    private static SecretKey key;

    @PostConstruct
    public void init(){
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public UserAuthDto generateTokens(String userName) {
        String accessToken = generateToken(userName, ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = generateToken(userName, REFRESH_TOKEN_EXPIRATION_TIME);

        return new UserAuthDto(accessToken, refreshToken);
    }

    private String generateToken(String userName, int expirationTime) {
        Claims claims = Jwts.claims()
                .subject(userName).build();

        Date curDate = new Date();
        Date expireDate = new Date(curDate.getTime() + expirationTime);

        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .issuedAt(curDate)
                .expiration(expireDate)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // Invalid signature or malformed token
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // Token is expired
            log.error("Expired JWT token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // Unsupported JWT
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // Empty or null token
            log.error("JWT token is empty: {}", e.getMessage());
        }
        return false;
    }

    public String getUserName(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
