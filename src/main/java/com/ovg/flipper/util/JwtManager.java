package com.ovg.flipper.util;

import com.ovg.flipper.dto.UserAuthDto;
import com.ovg.flipper.repository.RedisJwtRepository;
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
public class JwtManager {
    @Value("${jwt.secret}")
    private String secret;
    private static final int ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60; // 1hour
    private static final int REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; // 1week
    private static SecretKey key;

    private final RedisJwtRepository redisJwtRepository;

    public JwtManager(RedisJwtRepository redisJwtRepository) {
        this.redisJwtRepository = redisJwtRepository;
    }

    @PostConstruct
    public void init(){
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public UserAuthDto generateTokens(Long userId, String userName) {
        String accessToken = generateToken(userName, ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = generateToken(userName, REFRESH_TOKEN_EXPIRATION_TIME);

        redisJwtRepository.save(refreshToken, userId);

        return new UserAuthDto(accessToken, refreshToken);
    }

    public UserAuthDto generateTokens(String token) {
        Long userId = redisJwtRepository.get(token);
        String userName = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();

        return generateTokens(userId, userName);
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
        } catch (Exception e){
            log.warn("JWT token exception : {}", e.getMessage());
        }
        return false;
    }

    public String getUserName(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean checkRefreshTokenExists(String token){
        return redisJwtRepository.exists(token);
    }
}
