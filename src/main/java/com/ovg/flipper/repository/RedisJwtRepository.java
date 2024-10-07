package com.ovg.flipper.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisJwtRepository implements JwtRepository {

    private final RedisTemplate<String, Long> redisTemplate;

    public RedisJwtRepository(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String token, Long userId) {
        redisTemplate.opsForValue().set(token, userId);
    }

    @Override
    public boolean exists(String token) {
        return redisTemplate.hasKey(token) == Boolean.TRUE;
    }

    @Override
    public void delete(String token) {
        redisTemplate.delete(token);
    }

    @Override
    public Long get(String token) {
        return redisTemplate.opsForValue().get(token);
    }
}
